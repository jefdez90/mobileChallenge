# Discogs Artist Explorer

Android app that integrates with the [Discogs API](https://www.discogs.com/developers) to search for artists, browse their detail pages (including band members), and explore their discography with pagination and filtering.

**Screens:** Search → Artist Detail → Albums (paginated, filterable by year, genre, and label)

<video src="https://github.com/user-attachments/assets/28945325-add9-4579-b293-443fecde0241" controls width="320"></video>

---

## Setup

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17+
- A Discogs account

### Get a Personal Access Token

1. Log in at [discogs.com](https://www.discogs.com)
2. Go to **Settings → Developers → Generate new token**
3. Copy the token

### Configure

Create or edit `local.properties` in the project root:

```properties
DISCOGS_TOKEN=your_token_here
```

> `local.properties` is `.gitignored` — never commit your token.

Open the project in Android Studio, let Gradle sync, and run on a device or emulator (API 24+).

---

## Architecture

MVVM + Repository with a strict three-layer separation enforced by package boundaries:

```
presentation/   Compose screens + ViewModels
domain/         Models, repository interfaces, use cases, filtering logic
data/           Retrofit API, DTOs, PagingSource implementations, in-memory cache
```

**Domain is pure Kotlin** — no Android or framework imports. This makes it trivial to test and trivially portable.

Each screen follows a stateful/stateless composable split: `XScreen(viewModel)` collects state and delegates to `XContent(data, callbacks)`, which is a plain Compose function with no VM dependency. Previews live on `XContent`.

Dependency injection is handled by Hilt throughout. All `ViewModel`s receive their dependencies injected — no manual factories.

---

## Development Approach

### Pagination strategy

The discography uses **Paging 3 with a network-only `PagingSource`** — no Room. Offline caching would add a non-trivial amount of complexity (RemoteMediator, schema, migration) with no real payoff for this use case: the dataset doesn't need offline support and re-fetching is cheap.

`AlbumsPagingSource` calls `GET /artists/{id}/releases` and filters results in-place to `type == "master" && role == "Main"`, keeping only one entry per album title and excluding side appearances. The ViewModel exposes `Flow<PagingData<Album>>.cachedIn(viewModelScope)` — rotation-safe, zero duplicated loads.

### Release enrichment + progressive filter options

Album covers, genres, and labels require a second call: `GET /releases/{main_release_id}`. `EnrichAlbumUseCase` is called inline for each item inside `PagingSource.load()` — each page waits for all its enrichment calls to complete before being delivered to the UI. Results are cached in `ReleaseRepository` to avoid redundant API calls. Filter options (year, genre, label) accumulate in `FilterOptionsRepository` as pages load, so genre and label chips appear progressively as the user scrolls through the discography.

### Local filtering

Filter state is a `MutableStateFlow<AlbumFilter>` in the ViewModel. When the user selects a chip, `flatMapLatest` rebuilds the `PagingData` stream applying `AlbumFilter.matches()` — a simple predicate covering year, genre, and label. Combining filters (e.g. year 1996 AND genre Rock) works via predicate composition with no extra API calls.

---

## Tests

```bash
./gradlew test
```

Unit tests cover the domain and data layers:

- **`AlbumFilterTest`** — filter predicate logic (year, genre, label, combinations, edge cases)
- **`EnrichAlbumUseCaseTest`** — enrichment flow, null `main_release` handling, filter accumulation
- **`FilterOptionsRepositoryImplTest`** — year/genre/label accumulation, deduplication

---

## Build Commands

```bash
./gradlew assembleDebug        # Build debug APK
./gradlew installDebug         # Build and install on connected device/emulator
./gradlew test                 # Run unit tests
./gradlew clean                # Clean build artifacts
```

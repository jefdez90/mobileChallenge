package com.discogs.mobilechallenge.presentation.artistdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.discogs.mobilechallenge.domain.model.BandMember
import com.discogs.mobilechallenge.ui.theme.MobileChallengeTheme

@Composable
internal fun MembersSection(members: List<BandMember>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Band Members",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        HorizontalDivider()
        members.forEach { member ->
            MemberRow(member = member)
        }
    }
}

@Composable
internal fun MemberRow(member: BandMember) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        SubcomposeAsyncImage(
            model = member.thumbnailUrl,
            contentDescription = member.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            error = { MemberPlaceholder() },
            loading = { MemberPlaceholder() },
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            if (!member.active) {
                Text(
                    text = "Former member",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MemberPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Preview(showBackground = true, name = "Active member")
@Composable
private fun MemberRowActivePreview() {
    MobileChallengeTheme {
        MemberRow(
            member = BandMember(id = 1, name = "Quique Rangel", active = true, thumbnailUrl = ""),
        )
    }
}

@Preview(showBackground = true, name = "Former member")
@Composable
private fun MemberRowFormerPreview() {
    MobileChallengeTheme {
        MemberRow(
            member = BandMember(id = 2, name = "Emmanuel del Real", active = false, thumbnailUrl = ""),
        )
    }
}

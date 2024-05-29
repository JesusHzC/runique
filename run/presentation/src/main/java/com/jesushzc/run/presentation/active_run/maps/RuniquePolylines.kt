package com.jesushzc.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline
import com.jesushzc.core.domain.location.LocationTimestamp

@Composable
fun RuniquePolylines(
    locations: List<List<LocationTimestamp>>
) {
    val polylines = remember(locations) {
        locations.map {
            it.zipWithNext { location1, location2 ->
                PolylineUi(
                    location1 = location1.locationWithAltitude.location,
                    location2 = location2.locationWithAltitude.location,
                    color = PolylineColorCalculator.locationsToColor(
                        location1 = location1,
                        location2 = location2
                    )
                )
            }
        }
    }

    polylines.forEach { polyline ->
        polyline.forEach { polylineUi ->
            Polyline(
                points = listOf(
                    LatLng(polylineUi.location1.lat, polylineUi.location1.long),
                    LatLng(polylineUi.location2.lat, polylineUi.location2.long),
                ),
                color = polylineUi.color,
                jointType = JointType.BEVEL
            )
        }
    }
}
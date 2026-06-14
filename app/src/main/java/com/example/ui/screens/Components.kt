package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PortfolioHistory
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================
// 1. Custom Vector Canvas Line Chart
// ==========================================

@Composable
fun PortfolioLineChart(
    historyList: List<PortfolioHistory>,
    modifier: Modifier = Modifier
) {
    if (historyList.size < 2) {
        // Fallback for insufficient data
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSlateCard)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "No Chart Data",
                    tint = DarkGreyText,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Chart initializes as you make transactions on Zelox",
                    color = DarkGreyText,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    // Capture values
    val netWorths = historyList.map { it.netWorth }
    val maxNetWorth = netWorths.maxOrNull() ?: 500.0
    val minNetWorth = netWorths.minOrNull() ?: 500.0

    // Range with 5% threshold padding
    val range = maxNetWorth - minNetWorth
    val paddingFactor = if (range == 0.0) 50.0 else range * 0.1
    val yMax = maxNetWorth + paddingFactor
    val yMin = Math.max(0.0, minNetWorth - paddingFactor)
    val yRange = yMax - yMin

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PORTFOLIO TREND (REAL-TIME)",
                        fontSize = 11.sp,
                        color = DarkGreyText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Current Value Track",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(EmeraldGreen, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active Ledger Sync",
                            color = EmeraldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas drawing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Gridlines (3 horizontal partitions)
                    val gridTiers = 3
                    for (i in 0..gridTiers) {
                        val y = (height / gridTiers) * i
                        drawLine(
                            color = BorderColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Compute points
                    val points = historyList.mapIndexed { idx, history ->
                        val x = (width / (historyList.size - 1)) * idx
                        val normY = if (yRange != 0.0) {
                            (history.netWorth - yMin) / yRange
                        } else 0.5
                        val y = height - (normY * height).toFloat()
                        Offset(x, y)
                    }

                    // Path lines
                    if (points.isNotEmpty()) {
                        val strokePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val current = points[i]
                                val prev = points[i - 1]
                                val cpX = (current.x + prev.x) / 2f
                                cubicTo(cpX, prev.y, cpX, current.y, current.x, current.y)
                            }
                        }

                        // Gradient Fill Path below the curves
                        val fillPath = Path().apply {
                            addPath(strokePath)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }

                        // Draw Gradient Area Fill
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(EmeraldGreen.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )

                        // Draw Outline Stroke Path
                        drawPath(
                            path = strokePath,
                            color = EmeraldGreen,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Draw Pulse tracker at the latest/final coordinate point
                        val finalPoint = points.last()
                        drawCircle(
                            color = EmeraldGreen,
                            radius = 6.dp.toPx(),
                            center = finalPoint
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = finalPoint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timeframe metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val startTimeStr = formatter.format(Date(historyList.first().timestamp))
                val endTimeStr = formatter.format(Date(historyList.last().timestamp))

                Text(
                    text = "Beginning: $startTimeStr",
                    color = DarkGreyText,
                    fontSize = 11.sp
                )
                Text(
                    text = "Latest ticker: $endTimeStr",
                    color = DarkGreyText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==========================================
// 2. M3 Dark Terminal Responsive Navigation Options bar
// ==========================================

@Composable
fun InteractiveBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = DarkSlateCard,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = { onNavigate("dashboard") },
            label = { Text("Portfolio", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreen,
                selectedTextColor = EmeraldGreen,
                indicatorColor = EmeraldGreen.copy(alpha = 0.15f),
                unselectedIconColor = DarkGreyText,
                unselectedTextColor = DarkGreyText
            ),
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Portfolio"
                )
            },
            modifier = Modifier.testTag("nav_portfolio")
        )

        NavigationBarItem(
            selected = currentRoute == "market",
            onClick = { onNavigate("market") },
            label = { Text("Market", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreen,
                selectedTextColor = EmeraldGreen,
                indicatorColor = EmeraldGreen.copy(alpha = 0.15f),
                unselectedIconColor = DarkGreyText,
                unselectedTextColor = DarkGreyText
            ),
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Market"
                )
            },
            modifier = Modifier.testTag("nav_market")
        )

        NavigationBarItem(
            selected = currentRoute == "transactions",
            onClick = { onNavigate("transactions") },
            label = { Text("History", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreen,
                selectedTextColor = EmeraldGreen,
                indicatorColor = EmeraldGreen.copy(alpha = 0.15f),
                unselectedIconColor = DarkGreyText,
                unselectedTextColor = DarkGreyText
            ),
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "History"
                )
            },
            modifier = Modifier.testTag("nav_history")
        )
    }
}

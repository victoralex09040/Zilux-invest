package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.InvestmentViewModel
import com.example.ui.AuthState
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: InvestmentViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        InvestmentMarketApp(viewModel)
      }
    }
  }
}

@Composable
fun InvestmentMarketApp(viewModel: InvestmentViewModel) {
  val navController = rememberNavController()
  val snackbarHostState = remember { SnackbarHostState() }
  val currentBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = currentBackStackEntry?.destination?.route ?: "splash"

  // Capture all async actions (purchases, resets, validation errors) and render beautiful SnackBar indicators
  LaunchedEffect(Unit) {
    viewModel.actionResult.collect { result ->
      if (result.isSuccess) {
        snackbarHostState.showSnackbar(result.getOrNull() ?: "Success!")
      } else {
        snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Transaction failed.")
      }
    }
  }

  // Monitor active session; if user logs out, pop routes all the way back to authorization login
  val currentUsername by viewModel.currentUsername.collectAsState()
  LaunchedEffect(currentUsername) {
    if (currentUsername == null && currentRoute != "splash" && currentRoute != "login" && currentRoute != "signup") {
      navController.navigate("login") {
        popUpTo(0) { inclusive = true }
      }
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    bottomBar = {
      // Elegant detail: only show the botnav bar once the user is authorized/logged in!
      val showBottomBar = currentRoute in listOf("dashboard", "market", "transactions")
      if (showBottomBar) {
        InteractiveBottomBar(
          currentRoute = currentRoute,
          onNavigate = { route ->
            navController.navigate(route) {
              popUpTo(navController.graph.startDestinationId) { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          }
        )
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = "splash",
      modifier = Modifier.padding(innerPadding)
    ) {
      // 1. SPLASH ENTRY
      composable("splash") {
        val saved = viewModel.getSavedLogin()
        SplashScreen(
          onTimeout = {
            if (saved != null) {
              viewModel.login(saved.first, saved.second, rememberMe = true)
            } else {
              navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
              }
            }
          }
        )

        val authState by viewModel.loginState.collectAsState()
        LaunchedEffect(authState) {
          if (saved != null) {
            if (authState is AuthState.Success) {
              navController.navigate("dashboard") {
                popUpTo("splash") { inclusive = true }
              }
              viewModel.clearAuthStates()
            } else if (authState is AuthState.Error) {
              navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
              }
              viewModel.clearAuthStates()
            }
          }
        }
      }

      // 2. LOG IN
      composable("login") {
        LoginScreen(
          viewModel = viewModel,
          onNavigateToSignUp = { navController.navigate("signup") },
          onLoginSuccess = {
            navController.navigate("dashboard") {
              popUpTo("login") { inclusive = true }
            }
          }
        )
      }

      // 3. SIGN UP
      composable("signup") {
        SignUpScreen(
          viewModel = viewModel,
          onNavigateToLogin = { navController.navigate("login") },
          onSignUpSuccess = {
            navController.navigate("dashboard") {
              popUpTo("signup") { inclusive = true }
            }
          }
        )
      }

      // 4. MAIN USER PORTFOLIO DASHBOARD / WELCOME SCREEN
      composable("dashboard") {
        DashboardScreen(
          viewModel = viewModel,
          onNavigateToPlanDetail = { planId ->
            if (planId == "NAVIGATE_ADMIN_PANEL") {
              navController.navigate("admin_panel")
            } else {
              navController.navigate("plan_detail/$planId")
            }
          },
          onNavigateToMarket = {
            navController.navigate("market") {
              popUpTo("dashboard") { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          }
        )
      }

      // 5. INVESTMENT PLANS MARKET
      composable("market") {
        MarketScreen(
          viewModel = viewModel,
          onNavigateToPlanDetail = { planId ->
            navController.navigate("plan_detail/$planId")
          }
        )
      }

      // 6. DETAILED TRANSACTION JOURNAL / AUDIT HISTORY
      composable("transactions") {
        TransactionsScreen(viewModel = viewModel)
      }

      // 7. PLAN DETAIL / REAL-TIME QUANT BUY AND SELL CALCULATOR
      composable(
        route = "plan_detail/{planId}",
        arguments = listOf(navArgument("planId") { type = NavType.StringType })
      ) { backStackEntry ->
        val planId = backStackEntry.arguments?.getString("planId") ?: ""
        PlanDetailScreen(
          planId = planId,
          viewModel = viewModel,
          onBack = { navController.popBackStack() }
        )
      }

      // 8. SECURE ADMINISTRATIVE PANEL GATED ROUTE
      composable("admin_panel") {
        AdminPanelScreen(
          viewModel = viewModel,
          onBack = { navController.popBackStack() }
        )
      }
    }
  }
}

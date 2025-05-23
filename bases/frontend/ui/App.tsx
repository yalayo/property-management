import { Switch, Route, useLocation } from "wouter";
import { queryClient } from "./lib/queryClient";
import { Toaster } from "../ui/toaster";
import NotFound from "@/pages/not-found";
import Home from "@/pages/home";
import Dashboard from "@/pages/dashboard";
import WaitingList from "@/pages/waiting-list";
import Payment from "@/pages/payment";
import Checkout from "@/pages/checkout";
import Subscribe from "@/pages/subscribe";
import PaymentSuccess from "@/pages/payment-success";
import SubscriptionSuccess from "@/pages/subscription-success";
import SurveyResults from "@/pages/survey-results";
import Onboarding from "@/pages/onboarding";
import FeaturesPricing from "@/pages/features-pricing";
import AdminLogin from "@/pages/admin-login";
import AdminDashboard from "@/pages/admin/dashboard";
import AdminSurveyAnalytics from "@/pages/admin/survey-analytics";
import AdminWaitingList from "@/pages/admin/waiting-list";
import Login from "@/pages/login";
import Register from "@/pages/register";
import BankAccounts from "@/pages/bank-accounts";
import SubscriptionTiers from "@/pages/subscription-tiers";
import TenantOnboarding from "@/pages/tenant-onboarding";
import ChangePassword from "@/pages/change-password";
import { AuthProvider } from "../../hooks/use-auth";
import { ProtectedRoute } from "../protected-route";
import { ChatbotWidget } from "../chatbot/ChatbotWidget";

function Router() {
  return (
    <Switch>
      {/* Public routes */}
      <Route path="/" component={Home} />
      <Route path="/login" component={Login} />
      <Route path="/register" component={Register} />
      <Route path="/waiting-list" component={WaitingList} />
      <Route path="/payment/:tier" component={Payment} />
      <Route path="/checkout" component={Checkout} />
      <Route path="/subscribe" component={Subscribe} />
      <Route path="/payment-success" component={PaymentSuccess} />
      <Route path="/subscription-success" component={SubscriptionSuccess} />
      <Route path="/survey-results" component={SurveyResults} />
      <Route path="/features-pricing" component={FeaturesPricing} />
      
      {/* Protected user routes */}
      <ProtectedRoute path="/dashboard" component={Dashboard} />
      <ProtectedRoute path="/onboarding" component={Onboarding} />
      <ProtectedRoute path="/bank-accounts" component={BankAccounts} />
      <ProtectedRoute path="/subscription-tiers" component={SubscriptionTiers} />
      <ProtectedRoute path="/tenant-onboarding" component={TenantOnboarding} />
      <ProtectedRoute path="/tenant-onboarding/:tenantId" component={TenantOnboarding} />
      <ProtectedRoute path="/properties/:propertyId/tenant-onboarding" component={TenantOnboarding} />
      <ProtectedRoute path="/change-password" component={ChangePassword} />
      
      {/* Admin routes */}
      <Route path="/admin-login" component={AdminLogin} />
      <Route path="/admin/dashboard" component={AdminDashboard} />
      <Route path="/admin/survey-analytics" component={AdminSurveyAnalytics} />
      <Route path="/admin/waiting-list" component={AdminWaitingList} />
      
      {/* Fallback route */}
      <Route component={NotFound} />
    </Switch>
  );
}

function App() {
  const [location] = useLocation();
  const showChatbot = !location.startsWith("/admin") && 
                      !location.startsWith("/login") && 
                      !location.startsWith("/register") && 
                      !location.startsWith("/waiting-list") && 
                      !location.startsWith("/payment") &&
                      !location.startsWith("/checkout") &&
                      !location.startsWith("/subscribe") &&
                      !location.startsWith("/payment-success") &&
                      !location.startsWith("/subscription-success") &&
                      !location.startsWith("/survey-results") &&
                      !location.startsWith("/features-pricing") &&
                      !location.startsWith("/admin-login") &&
                      location !== "/";

  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router />
        {showChatbot && <ChatbotWidget />}
        <Toaster />
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;

import { Route, BrowserRouter, Routes } from "react-router-dom"
import "./App.css"
import { Home } from "./pages/Home"
import { Login } from "./pages/Login"
import { My } from "./pages/My"
import { MyReservationList } from "./pages/MyReservationList"
import { EditProfile } from "./pages/EditProfile"
import { ShowDetail } from "./pages/ShowDetail"
import { ShowReservation } from "./pages/ShowReservation"
import Category from "./pages/Category"
import { Join } from "./pages/Join"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import PaymentPage from "./pages/PaymentPage"
import PaymentSuccess from "./pages/PaymentSuccess"
import PaymentFail from "./pages/PaymentFail"
import { Toaster } from "./components/ui/toaster"

const queryClient = new QueryClient()

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <div className="flex flex-col min-h-screen">
          <div className="flex-1">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/join" element={<Join />} />
              <Route path="/my" element={<My />} />
              <Route path="/my/reservation" element={<MyReservationList />} />
              <Route path="/my/edit-profile" element={<EditProfile />} />
              <Route path="/show/:id" element={<ShowDetail />} />
              <Route path="/category/:genre" element={<Category />} />
              <Route
                path="/show/:id/reservation"
                element={<ShowReservation />}
              />
              <Route path="/show/payment" element={<PaymentPage />} />
              <Route path="/payment/success" element={<PaymentSuccess />} />
              <Route path="/payment/fail" element={<PaymentFail />} />
            </Routes>
          </div>
        </div>
        <Toaster />
      </BrowserRouter>
    </QueryClientProvider>
  )
}

export default App

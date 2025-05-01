import { Route, BrowserRouter, Routes } from "react-router-dom"
import "./App.css"
import { Header } from "./layout/Header"
import { Home } from "./pages/Home"
import { Login } from "./pages/Login"
import { My } from "./pages/My"
import { PerformanceDetail } from "./pages/PerformanceDetail"
import { PerformanceReservation } from "./pages/PerformanceReservation"

function App() {
  return (
    <BrowserRouter>
      <div>
        <Header />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/my" element={<My />} />
          <Route path="/performance/:id" element={<PerformanceDetail />} />
          <Route
            path="/performance/:id/reservation"
            element={<PerformanceReservation />}
          />
        </Routes>
      </div>
    </BrowserRouter>
  )
}

export default App

import { Route, BrowserRouter, Routes } from "react-router-dom"
import "./App.css"
import { Header } from "./layout/Header"
import { Home } from "./pages/Home"
import { Login } from "./pages/Login"
import { My } from "./pages/My"
import { ShowDetail } from "./pages/ShowDetail"
import { ShowReservation } from "./pages/ShowReservation"
import { Footer } from "./layout/Footer"
import Category from "./pages/Category"
import { Join } from "./pages/Join"

function App() {
  return (
    <BrowserRouter>
      <div className="flex flex-col min-h-screen">
        <Header />
        <div className="flex-1">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/join" element={<Join />} />
            <Route path="/my" element={<My />} />
            <Route path="/show/:id" element={<ShowDetail />} />
            <Route path="/category/:genre" element={<Category />} />
            <Route path="/show/:id/reservation" element={<ShowReservation />} />
          </Routes>
        </div>
        <Footer />
      </div>
    </BrowserRouter>
  )
}

export default App

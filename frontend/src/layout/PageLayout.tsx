import { Footer } from "./Footer"
import { Header } from "./Header"

interface PageLayoutProps {
  children: React.ReactNode
}

export const PageLayout = ({ children }: PageLayoutProps) => {
  return (
    <main className="h-full bg-white">
      <Header />
      {children}
      <Footer />
    </main>
  )
}

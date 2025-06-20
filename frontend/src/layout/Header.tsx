import { Button } from "@/components/ui/button"
import { useUser } from "@/hooks/useUser"
import { Link } from "react-router-dom"

export const Header = () => {
  const { isLogin, logout } = useUser()

  return (
    <header className="border-b">
      <div className="container mx-auto px-4 py-4 flex justify-between items-center">
        <Link to="/" className="text-xl font-bold flex items-center gap-2">
          <img src="/logo_64.png" alt="logo" className="w-10 h-10" />
          Pick-Go
        </Link>
        {isLogin ? (
          <div className="flex gap-2">
            <Button variant="outline" size="sm">
              <Link to="/my">마이페이지</Link>
            </Button>
            <Button
              className="cursor-pointer"
              size="sm"
              onClick={() => logout()}
            >
              로그아웃
            </Button>
          </div>
        ) : (
          <div className="flex gap-2">
            <Button variant="outline" size="sm">
              <Link to="/login">로그인</Link>
            </Button>
            <Button size="sm">
              <Link to="/join">회원가입</Link>
            </Button>
          </div>
        )}
      </div>
    </header>
  )
}

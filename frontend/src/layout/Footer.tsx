import { Link } from "react-router-dom"

export const Footer = () => {
  return (
    <footer className="border-t py-8">
      <div className="container mx-auto px-4">
        <div className="flex justify-center gap-6 text-sm text-gray-500 mb-4">
          <Link href="#" className="hover:text-gray-700">
            회사 정보
          </Link>
          <Link href="#" className="hover:text-gray-700">
            이용약관
          </Link>
          <Link href="#" className="hover:text-gray-700">
            개인정보처리방침
          </Link>
          <Link href="#" className="hover:text-gray-700">
            고객센터
          </Link>
        </div>
        <p className="text-center text-xs text-gray-400">
          © 2025 티켓 예매 사이트. All rights reserved.
        </p>
      </div>
    </footer>
  )
}

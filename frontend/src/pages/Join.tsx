import type React from "react";

import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";
import { PageLayout } from "@/layout/PageLayout";
import { apiClient } from "@/api/apiClient";

export function Join() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    confirmPassword: "",
    nickname: "",
    email: "",
    code: "",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isEmailVerified, setIsEmailVerified] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));

    // 입력 시 해당 필드의 에러 메시지 제거
    if (errors[name]) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    // 비밀번호 검증
    if (!formData.password) {
      newErrors.password = "비밀번호를 입력해주세요.";
    }

    // 비밀번호 확인 검증
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "비밀번호가 일치하지 않습니다.";
    }

    // 닉네임 검증
    if (!formData.nickname.trim()) {
      newErrors.nickname = "닉네임을 입력해주세요.";
    }

    // 이메일 검증
    if (!formData.email.trim()) {
      newErrors.email = "이메일을 입력해주세요.";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "올바른 이메일 형식이 아닙니다.";
    }

    if (!isEmailVerified) {
      newErrors.email = "이메일 인증을 완료해주세요.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    setIsSubmitting(true);

    try {
      console.log("회원가입 데이터:", {
        username: formData.username,
        password: formData.password,
        nickname: formData.nickname,
        email: formData.email,
      });

      const response = await apiClient.member.signup({
        memberCreateRequest: {
          email: formData.email,
          password: formData.password,
          nickname: formData.nickname,
        },
      });

      if (response.code !== 200) {
        throw new Error("회원가입에 실패했습니다.");
      }

      // 성공 시 로그인 페이지로 이동
      alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
      navigate("/login");
    } catch (error) {
      console.error("회원가입 오류:", error);
      alert("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // 인증번호 요청
  const handleSendEmailCode = async () => {
    try {
      await apiClient.member.sendCode({ email: formData.email });
      alert("이메일로 인증번호가 발송되었습니다.");
    } catch (err) {
      alert("인증번호 발송에 실패했습니다.");
    }
  };

  // 이메일 인증번호 확인
  const handleVerifyEmailCode = async () => {
    try {
      const result = await apiClient.member.verifyEmailCode({
        email: formData.email,
        code: formData.code,
      });
      if (result.code === 200) {
        alert("이메일 인증이 완료되었습니다.");
        setIsEmailVerified(true);
      } else {
        alert("인증번호가 올바르지 않습니다.");
        setIsEmailVerified(false);
      }
    } catch {
      alert("이메일 인증 중 오류가 발생했습니다.");
    }
  };

  return (
    <PageLayout>
      <div className="h-full flex flex-col items-center justify-center px-4 py-12 bg-white">
        <div className="w-full max-w-md">
          <h1 className="text-2xl font-bold mb-6">회원가입</h1>

          <p className="text-gray-600 mb-8">
            저희 사이트에 오신 것을 환영합니다.
          </p>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* 이메일 입력 */}
            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                이메일
              </label>
              <div className="flex gap-2">
                <input
                  id="email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  className={`flex-1 w-full px-3 py-2 border ${
                    errors.email ? "border-red-500" : "border-gray-300"
                  } rounded-md focus:outline-none focus:ring-1 focus:ring-gray-900`}
                />
                <button
                  type="button"
                  onClick={handleSendEmailCode}
                  className="px-3 py-2 text-sm bg-black text-white rounded-md"
                >
                  인증요청
                </button>
              </div>
              {errors.email && (
                <p className="mt-1 text-sm text-red-500">{errors.email}</p>
              )}
            </div>

            {/* 인증코드 입력 */}
            <div>
              <label
                htmlFor="emailCode"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                인증번호
              </label>
              <div className="flex gap-2">
                <input
                  id="code"
                  name="code"
                  type="text"
                  value={formData.code}
                  onChange={handleChange}
                  className="flex-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-gray-900"
                />
                <button
                  type="button"
                  onClick={handleVerifyEmailCode}
                  className="px-3 py-2 text-sm bg-black text-white rounded-md"
                >
                  인증확인
                </button>
              </div>
            </div>

            {/* 비밀번호 입력 */}
            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                비밀번호
              </label>
              <div className="relative">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  value={formData.password}
                  onChange={handleChange}
                  className={`w-full px-3 py-2 border ${
                    errors.password ? "border-red-500" : "border-gray-300"
                  } rounded-md focus:outline-none focus:ring-1 focus:ring-gray-900`}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center"
                >
                  {showPassword ? (
                    <EyeOff className="h-5 w-5 text-gray-400" />
                  ) : (
                    <Eye className="h-5 w-5 text-gray-400" />
                  )}
                </button>
              </div>
              {errors.password && (
                <p className="mt-1 text-sm text-red-500">{errors.password}</p>
              )}
            </div>

            {/* 비밀번호 확인 */}
            <div>
              <label
                htmlFor="confirmPassword"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                비밀번호 확인
              </label>
              <div className="relative">
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className={`w-full px-3 py-2 border ${
                    errors.confirmPassword
                      ? "border-red-500"
                      : "border-gray-300"
                  } rounded-md focus:outline-none focus:ring-1 focus:ring-gray-900`}
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center"
                >
                  {showConfirmPassword ? (
                    <EyeOff className="h-5 w-5 text-gray-400" />
                  ) : (
                    <Eye className="h-5 w-5 text-gray-400" />
                  )}
                </button>
              </div>
              {errors.confirmPassword && (
                <p className="mt-1 text-sm text-red-500">
                  {errors.confirmPassword}
                </p>
              )}
            </div>

            {/* 닉네임 입력 */}
            <div>
              <label
                htmlFor="nickname"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                닉네임
              </label>
              <input
                id="nickname"
                name="nickname"
                type="text"
                value={formData.nickname}
                onChange={handleChange}
                className={`w-full px-3 py-2 border ${
                  errors.nickname ? "border-red-500" : "border-gray-300"
                } rounded-md focus:outline-none focus:ring-1 focus:ring-gray-900`}
              />
              {errors.nickname && (
                <p className="mt-1 text-sm text-red-500">{errors.nickname}</p>
              )}
            </div>

            {/* 회원가입 버튼 */}
            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full bg-black text-white py-3 rounded-md hover:bg-gray-800 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-900 focus:ring-offset-2 disabled:opacity-70"
            >
              {isSubmitting ? "처리 중..." : "회원가입"}
            </button>
          </form>

          {/* 로그인 링크 */}
          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              이미 계정이 있으신가요?{" "}
              <Link to="/login" className="text-blue-600 hover:underline">
                로그인
              </Link>
            </p>
          </div>
        </div>
      </div>
    </PageLayout>
  );
}

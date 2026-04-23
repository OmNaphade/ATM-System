import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/authApi";

export default function Login() {
  const [accountNumber, setAccountNumber] = useState("");
  const [pin, setPin] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    if (!accountNumber || !pin) {
      setError("Please enter Account Number and PIN");
      return;
    }

    try {
      const accountData = await login(accountNumber, pin);

      if (accountData) {
        localStorage.setItem("accountNumber", accountNumber);
        localStorage.setItem("accountId", accountData.accountId);
        localStorage.setItem(
          "accountData",
          JSON.stringify(accountData)
        );
        navigate("/dashboard");
      }
    } catch (err) {
      setError(err.message || "Account not found");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-900 to-slate-800">
      <div className="card w-80">
        <h2 className="text-2xl font-bold text-center mb-6">ATM Login</h2>

        <form onSubmit={handleLogin} className="space-y-4">
          <input
            type="text"
            placeholder="Account Number"
            value={accountNumber}
            onChange={(e) => setAccountNumber(e.target.value)}
            className="input"
            required
          />

          <input
            type="password"
            placeholder="PIN"
            value={pin}
            onChange={(e) => setPin(e.target.value)}
            className="input"
            required
          />

          <button className="btn-primary w-full">Login</button>
        </form>

        {error && (
          <p className="text-red-400 text-sm mt-4 text-center">
            {error}
          </p>
        )}
      </div>
    </div>
  );
}

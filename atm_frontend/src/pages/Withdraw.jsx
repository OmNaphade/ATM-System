import { useState } from "react";
import { createTransaction } from "../api/transactionApi";

export default function Withdraw() {
  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");

  const accountId = localStorage.getItem("accountId");

  const handleWithdraw = async (e) => {
    e.preventDefault();

    try {
      await createTransaction({
        accountId: Number(accountId),
        amount: Number(amount),
        type: "WITHDRAW",
      });

      setMessage("✅ Withdrawal successful");
      setAmount("");
    } catch (err) {
      setMessage("❌ Withdrawal failed");
    }
  };

  return (
    <div className="card max-w-md mx-auto mt-10">
      <h2 className="text-2xl font-bold mb-4 text-center">
        Withdraw Cash
      </h2>

      <form onSubmit={handleWithdraw} className="space-y-4">
        <input
          type="number"
          placeholder="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="input"
          required
        />

        <button className="btn-danger w-full">
          Withdraw
        </button>
      </form>

      {message && <p className="mt-4 text-center">{message}</p>}
    </div>
  );
}

import { useState } from "react";
import { createTransaction } from "../api/transactionApi";

export default function Deposit() {
  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");

  const accountId = localStorage.getItem("accountId");

  const handleDeposit = async (e) => {
    e.preventDefault();

    try {
      await createTransaction({
        accountId: Number(accountId),
        amount: Number(amount),
        type: "DEPOSIT",
      });

      setMessage("✅ Deposit successful");
      setAmount("");
    } catch (err) {
      setMessage("❌ Deposit failed");
    }
  };

  return (
    <div className="card max-w-md mx-auto mt-10">
      <h2 className="text-2xl font-bold mb-4 text-center">
        Deposit Cash
      </h2>

      <form onSubmit={handleDeposit} className="space-y-4">
        <input
          type="number"
          placeholder="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="input"
          required
        />

        <button className="btn-success w-full">
          Deposit
        </button>
      </form>

      {message && <p className="mt-4 text-center">{message}</p>}
    </div>
  );
}

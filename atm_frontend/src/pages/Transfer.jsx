import { useState } from "react";
import { createTransaction } from "../api/transactionApi";
import { useNavigate } from "react-router-dom";

export default function Transfer() {
  const [fromAccountId, setFromAccountId] = useState("");
  const [toAccountId, setToAccountId] = useState("");
  const [amount, setAmount] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    if (!fromAccountId || !toAccountId || !amount) {
      setError("All fields are required.");
      return;
    }
    if (fromAccountId === toAccountId) {
      setError("Source and destination accounts must be different.");
      return;
    }
    try {
      await createTransaction({
        accountId: Number(fromAccountId),
        toAccountId: Number(toAccountId),
        amount: Number(amount),
        type: "TRANSFER",
      });
      setSuccess("Transfer successful!");
      setTimeout(() => navigate("/dashboard"), 1200);
    } catch (err) {
      setError(err?.response?.data?.message || "Transfer failed.");
    }
  };

  return (
    <div className="flex flex-col items-center mt-10">
      <h2 className="text-2xl font-bold mb-4">Transfer Funds</h2>
      <form onSubmit={handleSubmit} className="w-full max-w-sm space-y-4">
        <input
          type="number"
          placeholder="From Account ID"
          value={fromAccountId}
          onChange={e => setFromAccountId(e.target.value)}
          className="input input-bordered w-full"
        />
        <input
          type="number"
          placeholder="To Account ID"
          value={toAccountId}
          onChange={e => setToAccountId(e.target.value)}
          className="input input-bordered w-full"
        />
        <input
          type="number"
          placeholder="Amount"
          value={amount}
          onChange={e => setAmount(e.target.value)}
          className="input input-bordered w-full"
        />
        {error && <div className="text-red-500">{error}</div>}
        {success && <div className="text-green-600">{success}</div>}
        <button type="submit" className="btn btn-primary w-full">Transfer</button>
      </form>
    </div>
  );
}


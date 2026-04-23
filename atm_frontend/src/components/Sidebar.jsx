import { useLocation, useNavigate } from "react-router-dom";

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();

  const menu = [
    { name: "Login", path: "/login" },
    { name: "Dashboard", path: "/dashboard" },
    { name: "Deposit", path: "/deposit" },
    { name: "Withdraw", path: "/withdraw" },
  ];

  const handleLogout = () => {
    localStorage.removeItem("accountId");
    navigate("/");
  };

  return (
    <div className="fixed left-0 top-0 w-64 h-screen bg-gray-900 text-white flex flex-col justify-between p-5">

      {/* Top Section */}
      <div>
        <h2
          className="text-xl font-bold text-blue-400 mb-8 cursor-pointer"
          onClick={() => navigate("/dashboard")}
        >
          Navigation
        </h2>

        <div className="space-y-2">
          {menu.map((item) => (
            <div
              key={item.path}
              onClick={() => navigate(item.path)}
              className={`p-3 rounded-lg cursor-pointer transition ${
                location.pathname === item.path
                  ? "bg-blue-600"
                  : "hover:bg-gray-700"
              }`}
            >
              {item.name}
            </div>
          ))}
        </div>
      </div>

      {/* Bottom Section (Profile + Logout) */}
      <div className="space-y-3">

        {/* Profile */}
        <div
          onClick={() => navigate("/profile")}
          className={`p-3 rounded-lg cursor-pointer transition ${
            location.pathname === "/profile"
              ? "bg-blue-600"
              : "hover:bg-gray-700"
          }`}
        >
          Profile
        </div>

        {/* Account Info (optional) */}
        <div className="text-sm text-gray-400 px-2">
          Acc: {localStorage.getItem("accountId")}
        </div>

        {/* Logout */}
        <button
          onClick={handleLogout}
          className="w-full bg-red-500 py-2 rounded-lg hover:bg-red-600"
        >
          Logout
        </button>
      </div>
    </div>
  );
}

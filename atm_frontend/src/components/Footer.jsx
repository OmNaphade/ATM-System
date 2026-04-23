export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-300 text-center py-3 mt-auto">
      <p className="text-sm">
        © {new Date().getFullYear()} ATM System • All Rights Reserved
      </p>
    </footer>
  );
}

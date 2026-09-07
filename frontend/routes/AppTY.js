import { Routes, Route } from "react-router-dom";

import Home from "../frontend/src/pages/home/home";
import Login from "./pages/Login"; /*<-- Corrigido Futuramente*/
import Dashboard from "./pages/Dashboard"; /*<-- Corrigido Futuramente*/

export default function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} /> /*-- Corrigido Futuramente*/
            <Route path="/dashboard" element={<Dashboard />} /> /*-- Corrigido Futuramente*/
        </Routes>
    );
}
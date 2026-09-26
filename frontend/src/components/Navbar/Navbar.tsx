import { useState } from 'react'
import { Link } from 'react-router-dom'

function Navbar () {
    const [menuOpen, setMenuOpen] = useState(false)
    
    function toggleMenu() {
        setMenuOpen(!menuOpen)
    }
}

export default 'Navbar'
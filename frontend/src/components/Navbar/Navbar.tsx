import { useState } from 'react'
import { Link } from 'react-router-dom'

function Navbar () {
    const [menuOpen, setMenuOpen] = useState(false)
    
    function toggleMenu() {
        setMenuOpen(!menuOpen)
    }

    return (
        <header className="Navbar">
            <Link to="/" className="navbar__logo">
                Projects Maneger
            </Link>

            <nav className="Navbar_links" aria-label="Navegaçao Principal">
                <Link to="/">Pesquisar</Link>
                <Link to="">Gerir Todos os Projetos</Link>
                <Link to="">Filtros</Link> {/* <-- Corrigido Futuramente */}
                <Link to="">Recentes</Link> 
                <Link to="">Pessoas</Link>
                <Link to="">Ordenar</Link> {/* <-- Corrigido Futuramente */}
                <Link to="">Ocultar</Link> {/* <-- Corrigido Futuramente */}
                <Link to=""></Link> {/* <-- Corrigido Futuramente */}
            </nav>
        </header>
    )
}

export default 'Navbar'
export async function loadComponent(elementId, path) {
    const element = document.getElementById(elementId);

    if (!element) {
        console.error(`Elemento #${elementId} não encontrado.`);
        return;
    }

    try {
        const response = await fetch(path);

        if (!response.ok) {
            throw new Error(`Falha ao carregar ${path}: HTTP ${response.status}`);
        }

        element.innerHTML = await response.text();
    } catch (error) {
        console.error(error);
    }
}

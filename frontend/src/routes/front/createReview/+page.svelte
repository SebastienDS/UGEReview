<script>
import { goto } from '$app/navigation';
import NavBar from '$lib/components/NavBar.svelte';

const form = {
    title: '',
    commentary: '',
    code: '',
    test: ''
}

async function createReview() {
    try {
        const response = await fetch("/api/v1/createReview", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(form)
        });
        const createdReview = await response.json()
        goto('/front/reviews/' + createdReview.id);
    } catch (error) {
        console.error(error);
    }
}
</script>

<div class="container">
    <NavBar/>

    <h1>UGERevue</h1>

    <form on:submit|preventDefault={createReview} class="mb-5">
        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-person-fill" viewBox="0 0 16 16">
                          <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6"/>
                        </svg>
                    </span>
                </div>
                <input type="text" class="form-control" id="title" placeholder="Titre :" bind:value={form.title}>
            </div>
        </div>
        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-lock-fill" viewBox="0 0 16 16">
                          <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2m3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2"/>
                        </svg>
                    </span>
                </div>
                <input type="text" class="form-control" id="commentary" placeholder="Commentaire :" bind:value={form.commentary}>
            </div>
        </div>
        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-lock-fill" viewBox="0 0 16 16">
                          <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2m3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2"/>
                        </svg>
                    </span>
                </div>
                <input type="text" class="form-control" id="code" placeholder="Code :" bind:value={form.code}>
            </div>
        </div>
        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-lock-fill" viewBox="0 0 16 16">
                          <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2m3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2"/>
                        </svg>
                    </span>
                </div>
                <input type="text" class="form-control" id="test" placeholder="Test :" bind:value={form.test}>
            </div>
        </div>
        <div class="form-group mb-3">
            <button type="submit" class="btn btn-primary">Créer une review</button>
        </div>
    </form>
</div>
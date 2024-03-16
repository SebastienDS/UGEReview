<script>
import { goto } from '$app/navigation';
import { authToken } from '$lib/auth';
import Header from '$lib/components/Header.svelte';
import { userData } from '$lib/userData';

let username = '';
let password = '';

let error = false;

async function login() {
    try {
        const response = await fetch("/api/v1/login", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        if (!response.ok) {
            error = true;
            return
        }
        var user = await response.json();
        authToken.update(username, password);
        userData.update(user);
        
        goto('/front/reviews');
        error = false;
    } catch (error) {
        console.error(error);
        error = true;
    }
}

</script>

<div class="container">
    <Header/>

    <h1>UGERevue</h1>

    <h2>Se connecter</h2>

    <form on:submit|preventDefault={login} class="mb-5">
        {#if error}
            <div class="alert alert-danger">
                Invalide nom d'utilisateur ou mot de passe
            </div>
        {/if}

        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-person-fill" viewBox="0 0 16 16">
                        <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6"/>
                        </svg>
                    </span>
                </div>
                <input type="text" class="form-control" id="username" placeholder="Entrer le nom d'utilisateur" bind:value={username}>
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
                <input type="password" class="form-control" id="password" placeholder="Entrer le mot de passe" bind:value={password}>
            </div>
        </div>
        <div class="form-group mb-3">
            <a href="/front/resetPassword">Mot de passe oublié</a>
        </div>
        <div class="form-group mb-3">
            <a href="/front/signup">Créer un compte</a>
        </div>
        <div class="form-group mb-3">
            <button type="submit" class="btn btn-primary">Se connecter</button>
        </div>
    </form>
</div>

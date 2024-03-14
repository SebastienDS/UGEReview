<script>
    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';

    let username = '';
    let email = '';
    let password = '';

    let registered = false;
    let error = false;

    async function signup() {
        try {
            const response = await fetch("/api/v1/signup", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, email, password })
            });
            if (!response.ok) {
                error = true;
                return
            }
            registered = true;
            error = false;
        } catch (error) {
            console.error(error);
            error = true;
        }
    }
</script>

<div class="container">
    <h1>UGERevue</h1>

    <form on:submit|preventDefault={signup} class="mb-5">
        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-person-fill" viewBox="0 0 16 16">
                          <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6"/>
                        </svg>
                    </span>
                </div>
                <input type="text" name="username" class="form-control" id="username" placeholder="Entrer le nom d'utilisateur" bind:value={username}>
            </div>
        </div>
        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-envelope-fill" viewBox="0 0 16 16">
                          <path d="M.05 3.555A2 2 0 0 1 2 2h12a2 2 0 0 1 1.95 1.555L8 8.414zM0 4.697v7.104l5.803-3.558zM6.761 8.83l-6.57 4.027A2 2 0 0 0 2 14h12a2 2 0 0 0 1.808-1.144l-6.57-4.027L8 9.586zm3.436-.586L16 11.801V4.697z"/>
                        </svg>
                    </span>
                </div>
                <input type="text" name="email" class="form-control" id="email" placeholder="Entrer l'adresse email" bind:value={email}>
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
                <input type="password" name="password" class="form-control" id="password" placeholder="Entrer le mot de passe" bind:value={password}>
            </div>
        </div>
        <div class="form-group mb-3">
            <button type="submit" class="btn btn-primary">Submit</button>
        </div>
    </form>
</div>

{#if registered}
    <p>
        Le compte a bien été créé.
    </p>
{/if}
<script>
import NavBar from '$lib/components/NavBar.svelte';

let success = null;
let email = '';


async function resetPassword() {
    try {
        const response = await fetch("/api/v1/resetPassword", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email })
        });
        success = response.ok
    } catch (error) {
        console.error(error);
        success = false;
    }
}
</script>

<div class="container">
    <NavBar/>

    <h1>UGERevue</h1>

    <p>Mot de passe oublié</p>

    <form on:submit|preventDefault={resetPassword}>
        <div class="input-group mb-3">
            <input type="text" class="form-control" placeholder="Entrer l'adresse email de récupération" bind:value={email}>
            <div class="input-group-append">
                <button class="btn btn-primary" type="submit">Valider</button>
            </div>
        </div>
    </form>

    {#if success != null}
        {#if success}
            <div>
                Email de récupération du compte envoyé sur votre adresse email
            </div>
        {:else}
            <div>
                L'email ne correspond à aucun compte
            </div>
        {/if}
    {/if}
</div>
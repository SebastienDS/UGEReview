<script>
    import NavBar from '$lib/components/NavBar.svelte';

    export let data;

    let password = '';
    let passwordError = '';
    let success = false;

    
    async function resetPassword() {
        try {
            const response = await fetch(`/api/v1/resetPassword/${data.token}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ password })
            });
            password = '';
            if (response.ok) {
                success = true;
            } else {
                passwordError = (await response.json()).error
            }
        } catch (error) {
            console.error(error);
            passwordError = 'error'
        }
    }
</script>

<div class="container">
    <NavBar/>

    <h1>UGERevue</h1>

    <p>Mot de passe oublié</p>

    {#if !data.tokenValid}
        <p>Le token de récupération de compte est valide ou a expiré</p>
    {:else if success}
        <p>Le mot de passe a bien été changé</p>
    {:else}

        <form on:submit|preventDefault={resetPassword}>
            <div class="input-group mb-3">
                <input type="password" class="form-control" placeholder="Entrer le nouveau mot de passe" bind:value={password}>
                <div class="input-group-append">
                    <button class="btn btn-primary" type="submit">Valider</button>
                </div>
            </div>
        </form>

        {#if passwordError}
            <p>
                {passwordError}
            </p>
        {/if}

    {/if}
</div>
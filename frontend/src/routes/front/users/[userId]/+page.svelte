<script>
    import { authToken } from '$lib/auth';
    import NavBar from '$lib/components/NavBar.svelte';
    import { formatDate } from '$lib/utils';
    import { userData } from '$lib/userData';


    export let data;


    const isUserAdmin = userData.get() != null && userData.get().role === 'ADMIN';
    const isUserPageAdmin = data.user.role == 'ADMIN';


    let newUsername = data.user.username;
    let newEmail = data.user.email;
    let oldPassword = '';
    let newPassword = '';

    let editUsername = false;
    let editEmail = false;
    let editPassword = false;

    async function followFunction() {
        try {
            const url = data.checkFollow ? `/api/v1/users/${data.userId}/unfollow` : `/api/v1/users/${data.userId}/follow`;
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                },
            });
            if (!response.ok) return;
            data.checkFollow = !data.checkFollow;
        } catch (error) {
            console.error(error);
        }
    }

    async function updateUsername() {
        try {
            const response = await fetch(`/users/${data.userId}/updateUsername`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': authToken.get()
                },
                body: newUsername
            });
            if (response.ok) {
                data.user.username = newUsername
                authToken.updateUsername(newUsername);
            } else {
                newUsername = data.user.username
            }
        } catch (error) {
            console.error(error)
        }
        editUsername = false
    }

    async function updateEmail() {
        try {
            const response = await fetch(`/users/${data.userId}/updateEmail`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': authToken.get()
                },
                body: newEmail
            });
            if (response.ok) {
                data.user.email = newEmail
            } else {
                newEmail = data.user.email
            }
        } catch (error) {
            console.error(error)
        }
        editEmail = false
    }

    async function updatePassword() {
        try {
            const response = await fetch(`/users/${data.userId}/updatePassword`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': authToken.get()
                },
                body: JSON.stringify({ oldPassword, newPassword })
            });
            if (response.ok) {
                authToken.updatePassword(newPassword)
            }
        } catch (error) {
            console.error(error)
        }
        editPassword = false
        oldPassword = ''
        newPassword = ''
    }

    async function banProfile() {
        try {
            const response = await fetch(`/api/v1/banProfile`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': authToken.get()
                },
                body: data.userId
            });
            if (!response.ok) return 
        } catch (error) {
            console.error(error)
        }
    }
    
</script>

<div class="container">
    <NavBar/>

    {#if data.isAuthenticated && !data.isMyUserPage}
        <div>
            <form on:submit|preventDefault={followFunction} class="mb-5">
                <div class="form-group mb-3">
                    {#if data.checkFollow}
                        <button type="submit" class="btn btn-primary">Unfollow</button>
                    {:else}
                        <button type="submit" class="btn btn-primary">Follow</button>
                    {/if}
                </div>
            </form>
        </div>
    {/if}
</div>

<div class="container text-center">
    <div class="col">
        <div class="row">
            <a href="/front/users/{data.userId}/reviews">Revues</a>
        </div>
        <div class="row">
            <a href="/front/users/{data.userId}/comments">Commentaires</a>
        </div>
        <div class="row">
            <a href="/front/users/{data.userId}/responses">Réponses</a>
        </div>
        <div class="row">
            <a href="/front/users/{data.userId}/likes">Likes</a>
        </div>
    </div>
</div>
<div class="container my-3">
    <div class="row">
        <div class="col text-end">
            <p>image</p>
        </div>
        <div class="col">
            <div class="row">
                {#if !editUsername}
                    <p>
                        <span id="username" class="me-2">{data.user.username}</span>
                        {#if data.isMyUserPage}
                            <button class="btn btn-light" on:click|preventDefault={() => editUsername = true}>
                                <i class="fas fa-edit"></i>
                            </button>
                        {/if}
                    </p>
                {:else}
                    <form on:submit|preventDefault={updateUsername}>
                        <div class="col">
                            <input maxlength="30" style="width: 250px;" class="form-control me-2" bind:value={newUsername}> 
                        </div>
                        <div class="col">
                            <button class="btn btn-light" type="submit">
                                <i id="validateIconUsername" class="fas fa-check"></i>
                            </button>
                        </div>
                    </form>
                {/if}
            </div>
            {#if data.isMyUserPage}
                <div class="row">
                    {#if !editEmail}
                        <p>
                            <span id="email" class="me-2">{data.user.email}</span>
                            <button class="btn btn-light" on:click|preventDefault={() => editEmail = true}>
                                <i class="fas fa-edit"></i>
                            </button>
                        </p>
                    {:else}
                        <form on:submit|preventDefault={updateEmail}>
                            <div class="col">
                                <input id="emailInput" maxlength="30" style="width: 250px;" class="form-control me-2" bind:value={newEmail}>
                            </div>
                            <div class="col">
                                <button class="btn btn-light" type="submit">
                                    <i id="validateIconEmail" class="fas fa-check"></i>
                                </button>
                            </div>
                        </form>
                    {/if}
                </div>
            {/if}
            <div class="row">
                <span>{formatDate(new Date(data.user.dateCreation))}</span>
            </div>
            {#if data.isMyUserPage}
                <div class="row">
                    {#if !editPassword}
                        <p>
                            <span id="password" class="me-2">********</span>
                            <button class="btn btn-light" on:click|preventDefault={() => editPassword = true}>
                                <i class="fas fa-edit"></i>
                            </button>
                        </p>
                    {:else}
                        <form on:submit|preventDefault={updatePassword}>
                            <div class="col">
                                <input id="oldPasswordInput" placeholder="Old Password" maxlength="30" style="width: 250px;" class="form-control me-2" bind:value={oldPassword}>
                                <input id="newPasswordInput" placeholder="New Password" maxlength="30" style="width: 250px;" class="form-control me-2" bind:value={newPassword}>
                            </div>
                            <div class="col">
                                <button class="btn btn-light" type="submit">
                                    <i id="validateIconPassword" class="fas fa-check"></i>
                                </button>
                            </div>
                        </form>
                    {/if}
                    {#if data.isAuthenticated && data.isMyUserPage}
                        <div class="mb-3">
                            <a href="/front/logout">Se déconnecter</a>
                        </div>
                    {/if}
                    <div>
                        {#if data.isAuthenticated && data.isMyUserPage}
                        <div class="mb-3">
                            <a href="/front/deleteProfile">Supprimer mon compte</a>
                        </div>
                        {/if}
                    </div>
                </div>
            {/if}      
            {#if !isUserPageAdmin && isUserAdmin}
                <form on:submit|preventDefault={banProfile}>
                    <button type="submit">Bannir le compte</button>
                </form>
            {/if}
        </div>
    </div>
</div>

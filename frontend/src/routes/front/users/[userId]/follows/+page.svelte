<script>
    export let data;

    import { authToken } from '$lib/auth';
    import Header from '$lib/components/Header.svelte';

    async function unfollowFunction(userId) {
        try {
            const url = `/api/v1/users/${userId}/unfollow`;
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                },
            });
            if (!response.ok) return;
            data.follows = data.follows.filter(follow => follow.id != userId);
        } catch (error) {
            console.error(error);
        }
    }

    let pageNumber = 0;
    let pageSize = 5;

    async function changePages(modifier) {
        try {
            pageNumber =  Math.max(0, pageNumber + modifier)
            const response = await fetch("/api/v1/users/" + data.userId + "/follows?pageSize=" + pageSize + "&pageNumber=" + pageNumber, {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            var follows = await response.json();
            data.follows = follows
        } catch (error) {
            console.log(error)
        }
    }


</script>

<div class="container">
    <Header/>
    <div class="container">
        <h1>UGERevue</h1>
    
        <ul class="list-group">
            {#each data.follows as myFollow}
                <li class="list-group-item position-relative">
                    <div class="d-flex justify-content-between">
                        <a href="/users/{myFollow.id}" class="col-9">
                            <div>
                                <p>{myFollow.username}</p>
                            </div>
                        </a>
        
                        {#if data.isAuthenticated && data.isAuthenticated != null && data.isMyUserPage && data.isMyUserPage}
                            <form on:submit|preventDefault={() => unfollowFunction(myFollow.id)}  class="mb-3">
                                <div class="form-group mb-3">
                                    <button type="submit" class="btn btn-primary">Unfollow</button>
                                </div>
                            </form>
                        {/if}
                    </div>
                </li>
            {/each}
            <div class="d-flex justify-content-between">
                <button on:click={() => changePages(-1)} class="btn btn-secondary">Précédent</button>
                <button on:click={() => changePages(1)} class="btn btn-secondary">Suivant</button>
            </div>
        </ul>
    </div>
    
</div>
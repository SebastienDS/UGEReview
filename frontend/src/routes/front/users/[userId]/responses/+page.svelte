<script>
    export let data;

    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';
    import NavBar from '$lib/components/NavBar.svelte';

    const isAuthenticated = authToken.get() != null;

</script>

<div class="container">
    <NavBar/>

    <ul class="list-group mb-3">
        {#each data.responses as response}
            <li class="list-group-item position-relative">
                <a href="/front/reviews/{response.reviewId}#response_{response.id}" class="stretched-link">
                    {JSON.stringify(response)}
                </a>
            </li>
        {/each}
    </ul>
</div>
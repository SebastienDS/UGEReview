<script>
    export let data;

    import { authToken } from '$lib/auth';
    import NavBar from '$lib/components/NavBar.svelte';

    const isAuthenticated = authToken.get() != null;

</script>

<div class="container">
    <NavBar/>

    <ul class="list-group mb-3">
        {#each data.likes as like}
            <li class="list-group-item position-relative">
                <a href="/front/reviews/{like.reviewId}#{like.className}_{like.id}" class="stretched-link">
                    {JSON.stringify(like)}
                </a>
            </li>
        {/each}
    </ul>
</div>
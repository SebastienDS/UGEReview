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
        {#each data.reviews as review}
            <li class="list-group-item position-relative">
                <a href="/front/reviews/{review.id}" class="stretched-link">
                    {JSON.stringify(review)}
                </a>
            </li>
        {/each}
    </ul>
</div>
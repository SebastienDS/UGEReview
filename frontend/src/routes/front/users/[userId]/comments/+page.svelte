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
        {#each data.comments as comment}
            <li class="list-group-item position-relative">
                <a href="/front/reviews/{comment.reviewId}#comment_{comment.id}" class="stretched-link">
                    {JSON.stringify(comment)}
                </a>
            </li>
        {/each}
    </ul>
</div>
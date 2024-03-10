<script>
    export let data;

    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';
    import { formatDate } from '$lib/utils';
    import NavBar from '$lib/components/NavBar.svelte';

    const isAuthenticated = authToken.get() != null;

</script>

<div class="container">
    <NavBar/>

    <ul class="list-group mb-3">
        {#each data.reviews as review}
            <li class="list-group-item position-relative">
                <div class="row">
                    <a href="/front/reviews/{review.id}" class="col-9">
                        <div>
                            <p>{review.title}</p>
                        </div>
                    </a>
                    <div class="col-3" style="text-align:end;">
                        <a href="/front/users/{review.author.id}">
                            <p>{review.author.username}</p>
                        </a>
                        <p> {formatDate(new Date(review.date))}</p>
                    </div>
                </div>
            </li>
        {/each}
    </ul>
</div>
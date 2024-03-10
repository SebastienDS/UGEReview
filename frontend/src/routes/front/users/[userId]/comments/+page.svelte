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
        {#each data.comments as comment}
            <li class="list-group-item position-relative">
                <div class="row">
                    <a href="/front/reviews/{comment.reviewId}#comment_{comment.id}" class="col-9">
                        <div>
                            <p>{comment.content}</p>
                        </div>
                    </a>
                    <div class="col-3" style="text-align:end;">
                        <a href="/front/users/{comment.author.id}">
                            <p>{comment.author.username}</p>
                        </a>
                        <p> {formatDate(new Date(comment.date))}</p>
                    </div>
                </div>
            </li>
        {/each}
    </ul>
</div>
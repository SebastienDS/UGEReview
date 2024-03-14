<script>
    export let data;

    import { authToken } from '$lib/auth';
    import { formatDate } from '$lib/utils';
    import Header from '$lib/components/Header.svelte';

    const isAuthenticated = authToken.get() != null;

</script>

<div class="container">
    <Header/>

    <ul class="list-group mb-3">
        {#each data.likes as like}
            <li class="list-group-item position-relative">
                <div class="row">
                    <a href="/front/reviews/{like.reviewId}#{like.className}_{like.id}" class="col-9">
                        <div>
                            <p>{like.content}</p>
                        </div>
                    </a>
                    <div class="col-3" style="text-align:end;">
                        <a href="/front/users/{like.author.id}">
                            <p>{like.author.username}</p>
                        </a>
                        <p> {formatDate(new Date(like.date))}</p>
                    </div>
                </div>
            </li>
        {/each}
    </ul>
</div>
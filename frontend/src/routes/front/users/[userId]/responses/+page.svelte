<script>
    export let data;

    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';
    import { formatDate } from '$lib/utils';
    import Header from '$lib/components/Header.svelte';

    const isAuthenticated = authToken.get() != null;

</script>

<div class="container">
    <Header/>

    <ul class="list-group mb-3">
        {#each data.responses as response}
            <li class="list-group-item position-relative">
                <div class="row">
                    <a href="/front/reviews/{response.reviewId}#response_{response.id}" class="col-9">
                        <div>
                            <p>{response.content}</p>
                        </div>
                    </a>
                    <div class="col-3" style="text-align:end;">
                        <a href="/front/users/{response.author.id}">
                            <p>{response.author.username}</p>
                        </a>
                        <p> {formatDate(new Date(response.date))}</p>
                    </div>
                </div>
            </li>
        {/each}
    </ul>
</div>
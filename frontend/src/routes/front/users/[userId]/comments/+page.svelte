<script>
    export let data;

    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';
    import { formatDate } from '$lib/utils';
    import Header from '$lib/components/Header.svelte';

    const isAuthenticated = authToken.get() != null;

    let pageNumber = 0;
    let pageSize = 5;

    async function changeComments(modifier) {
        try {
            pageNumber =  Math.max(0, pageNumber + modifier)
            const response = await fetch("/api/v1/users/" + data.userId + "/comments?pageSize=" + pageSize + "&pageNumber=" + pageNumber, {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            var comments = await response.json();
            data.comments = comments
        } catch (error) {
            console.log(error)
        }
    }
</script>

<div class="container">
    <Header/>

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
        <div class="d-flex justify-content-between">
            <button on:click={() => changeComments(-1)} class="btn btn-secondary">Précédent</button>
            <button on:click={() => changeComments(1)} class="btn btn-secondary">Suivant</button>
        </div>
    </ul>
</div>
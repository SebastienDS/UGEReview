<script>
    export let data;

    import { authToken } from '$lib/auth';
    import { formatDate } from '$lib/utils';
    import Header from '$lib/components/Header.svelte';

    const isAuthenticated = authToken.get() != null;

    let pageNumber = 0;
    let pageSize = 5;

    async function changeLikes(modifier) {
        try {
            pageNumber =  Math.max(0, pageNumber + modifier)
            const response = await fetch("/api/v1/users/" + data.userId + "/likes?pageSize=" + pageSize + "&pageNumber=" + pageNumber, {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            var likes = await response.json();
            data.likes = likes
        } catch (error) {
            console.log(error)
        }
    }

</script>

<div class="container">
    <Header/>

    <ul class="list-group mb-3">
        {#each data.likes as like}
            <li class="list-group-item position-relative">
                <div class="row">
                    <a href="/front/reviews/{like.reviewId}#{like.className}_{like.id}" class="col-9">
                        <div>
                            <p class="text-break text-justify" style="white-space: pre-wrap;">{like.content}</p>
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
        <div class="d-flex justify-content-between">
            <button on:click={() => changeLikes(-1)} class="btn btn-secondary">Précédent</button>
            <button on:click={() => changeLikes(1)} class="btn btn-secondary">Suivant</button>
        </div>
    </ul>
</div>
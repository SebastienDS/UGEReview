<script>
    export let data;

    import { authToken } from '$lib/auth';
    import Header from '$lib/components/Header.svelte';
    import { formatDate } from '$lib/utils';


    const isAuthenticated = authToken.get() != null;

    let search = '';
    let pageNumber = 0;
    let pageSize = 5;

    async function searchReviews() {
        try {
            const response = await fetch("/api/v1/reviews?search=" + search, {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            var reviews = await response.json();
            data.reviews = reviews
            pageNumber = 0;
        } catch (error) {
            console.log(error)
        }
    }

    async function changeReviews(modifier) {
        try {
            pageNumber =  Math.max(0, pageNumber + modifier)
            const response = await fetch("/api/v1/reviews?search=" + search + "&pageSize=" + pageSize + "&pageNumber=" + pageNumber, {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            var reviews = await response.json();
            data.reviews = reviews
        } catch (error) {
            console.log(error)
        }
    }
</script>

<div class="container">
    <Header/>

    <h1>UGERevue</h1>

    {#if isAuthenticated}
        <p>Connected</p>
    {/if}

    <form on:submit|preventDefault={searchReviews}>
        <div class="input-group mb-3">
            <input type="text" class="form-control" placeholder="Search" bind:value={search}>
            <div class="input-group-append">
                <button class="btn btn-primary" type="submit">Submit</button>
            </div>
        </div>
    </form>

    <ul class="list-group mb-3">
        {#each data.reviews as review}
            <li class="list-group-item position-relative">
                <div class="row">
                    <a href="/front/reviews/{review.id}" class="col-9">{review.title}</a>
                    <div class="col-3" style="text-align:end;">
                        <a href="/front/users/{review.author.id}">{review.author.username}</a>
                        <p>{formatDate(new Date(review.date))}</p>
                    </div>
                </div>
            </li>
        {/each}
        <div class="d-flex justify-content-between">
            <button on:click={() => changeReviews(-1)} class="btn btn-secondary">Précédent</button>
            <button on:click={() => changeReviews(1)} class="btn btn-secondary">Suivant</button>
        </div>
    </ul>
</div>
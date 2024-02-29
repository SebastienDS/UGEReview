<script>
    export let data;

    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';
    import NavBar from '$lib/components/NavBar.svelte';


    const isAuthenticated = authToken.get() != null;

    let search = '';

    async function searchReviews() {
        try {
            const response = await fetch("/api/v1/reviews?search=" + search, {
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

    async function markAsRead(notificationId) {
        try {
            const response = await fetch(`/api/v1/notifications/${notificationId}/markAsRead`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!response.ok) return
            data.notifications = data.notifications.filter(n => n.id != notificationId)
        } catch (error) {
            console.log(error)
        }
    }

    async function markAsReadAndRedirect(notificationId, link) {
        markAsRead(notificationId)
        goto(link)
    }
</script>

<div class="container">
    <NavBar/>

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

    {#if isAuthenticated}
        <ul class="list-group mb-3">
            {#each data.notifications as notification}
                <li class="list-group-item list-group-item-action list-group-item-info d-flex align-items-center">
                    <form on:submit|preventDefault={() => markAsRead(notification.id)} class="me-3">
                        <button type="submit" class="btn-close" aria-label="Close"></button>
                    </form>
                    <form on:submit|preventDefault={() => markAsReadAndRedirect(notification.id, notification.link)} class="position-relative">
                        <button type="submit" class="stretched-link btn btn-link">
                            {JSON.stringify(notification)}
                        </button>
                    </form>
                </li>
            {/each}
        </ul>
    {/if}


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
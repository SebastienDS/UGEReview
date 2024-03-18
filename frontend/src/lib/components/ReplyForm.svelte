<script xmlns:on="http://www.w3.org/1999/xhtml">
    export let comment;
    export let responses;
    export let reviewId;

    import { authToken } from '$lib/auth';


    let isVisible = false;
    let textareaValue = "";

    let showComments = false
    let comments = []

    function answer() {
        isVisible = true;
    }

    function sendResponse() {
        var object = {
            "id": comment.id,
            "content": textareaValue
        };
        const url = "/api/v1/reviews/" + reviewId + "/response";
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(object)
        })
        .then(response => {
            if(response.ok){
                isVisible = false;
                textareaValue = "";
                const dataPromise = response.json();
                dataPromise.then(dataResponse => {
                    responses = [...responses, dataResponse]
                })

            } else {
                console.log(response);
            }
        })
        .catch(error => {
            console.error('Erreur lors de la requête PUT :', error);
        });
    }

    async function searchComments() {
        if (showComments) {
            showComments = false
            return
        }
        await fetchComments()
        showComments = true
    }

    async function fetchComments() {
        try {
            const response = await fetch(`/api/v1/searchComments?search=${textareaValue}`, {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!response.ok) return;
            comments = await response.json();
        } catch (error) {
            console.error(error)
        }
    }

    async function applyComment(comment) {
        textareaValue = comment
        showComments = false
    }
</script>
<div class="row">
    <div class="col-12">
        {#if !isVisible}
            <button style="width: inherit;" on:click={answer}>
                Répondre
            </button>
        {/if}
        {#if isVisible}
            <div class="row">
                <div class="row">
                    <div class="col-11 form-group">
                        <textarea bind:value={textareaValue} on:input={fetchComments} cols="80" class="form-control"></textarea>
                    </div>
                    <div class="col-1 form-group">
                        <button on:click={searchComments} class="w-100 h-100">
                            {#if showComments}
                                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-search-heart-fill" viewBox="0 0 16 16">
                                    <path d="M6.5 13a6.47 6.47 0 0 0 3.845-1.258h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1A6.47 6.47 0 0 0 13 6.5 6.5 6.5 0 0 0 6.5 0a6.5 6.5 0 1 0 0 13m0-8.518c1.664-1.673 5.825 1.254 0 5.018-5.825-3.764-1.664-6.69 0-5.018"/>
                                </svg>
                            {:else}
                                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-search-heart" viewBox="0 0 16 16">
                                    <path d="M6.5 4.482c1.664-1.673 5.825 1.254 0 5.018-5.825-3.764-1.664-6.69 0-5.018"/>
                                    <path d="M13 6.5a6.47 6.47 0 0 1-1.258 3.844q.06.044.115.098l3.85 3.85a1 1 0 0 1-1.414 1.415l-3.85-3.85a1 1 0 0 1-.1-.115h.002A6.5 6.5 0 1 1 13 6.5M6.5 12a5.5 5.5 0 1 0 0-11 5.5 5.5 0 0 0 0 11"/>
                                </svg>
                            {/if}
                        </button>
                    </div>
                </div>
                <div class="row">
                    <div class="col-12 form-group">
                        <button on:click={sendResponse} style="width: inherit;">
                            Envoyer
                        </button>
                    </div>
                </div>
                {#if showComments}
                    <div class="row">
                        <div class="col-12">
                            <ul class="list-group">
                                {#each comments as comment}
                                    <li class="list-group-item text-break text-justify" style="white-space: pre-wrap;" on:click|preventDefault={() => applyComment(comment)}>
                                        {comment}
                                    </li>
                                {/each}
                            </ul>
                        </div>
                    </div>
                {/if}
            </div>
        {/if}
    </div>
</div>

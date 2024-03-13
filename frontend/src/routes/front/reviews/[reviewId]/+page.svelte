<script>
    import { goto } from '$app/navigation';
    import { authToken } from '$lib/auth';
    import NavBar from '$lib/components/NavBar.svelte';
    import ReplyForm from '$lib/components/ReplyForm.svelte';
    import { userData } from '$lib/userData';
    import { formatDate } from '$lib/utils';
    import { onMount } from 'svelte';

    export let data;

    const isAuthenticated = authToken.get() != null;
    const isUserAdmin = userData.get() != null && userData.get().role === 'ADMIN'

    let commentValue = "";

    let editorId;
    let editor;
    let testEditorId;
    let testEditor;

    onMount(() => {
        let width = editorId.offsetWidth;
        let height = editorId.offsetHeight;
        let testWidth = testEditorId.offsetWidth;
        let testHeight = testEditorId.offsetHeight;

        editor = CodeMirror.fromTextArea(
            editorId, {
                mode:"text/x-java",
                theme: "dracula",
                lineNumbers: true,
                readOnly: true
            }
        );

        testEditor = CodeMirror.fromTextArea(
            testEditorId, {
                mode:"text/x-java",
                theme: "dracula",
                lineNumbers: true,
                readOnly: true
            }
        );

        editor.setSize(width, height);
        testEditor.setSize(testWidth, testHeight);
    });

    function comment() {
        if (!commentValue) {
          return;
        }
        const url = "/api/v1/reviews/" + data.reviewId + "/comment";
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: commentValue
        })
        .then(response => {
            if(response.ok){
                commentValue = "";
                console.log("OK");
                const dataPromise = response.json();
                dataPromise.then(dataResponse => {
                    data.review.comments = [...data.review.comments, dataResponse]
                })
            } else {
                console.log(response);
            }
        })
        .catch(error => {
            console.error('Erreur lors de la requête PUT :', error);
        });
    }

    async function deleteReview() {
        try {
            const res = await fetch(`/api/v1/deleteReview`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get(),
                    'Content-Type': 'application/json'
                },
                body: data.review.id
            });
            if (!res.ok) return
            goto('/front/reviews');
        } catch (error) {
            console.error(error);
        }
    }

    async function deleteComment(comment) {
        try {
            const res = await fetch(`/api/v1/deleteComment?reviewId=${data.reviewId}`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get(),
                    'Content-Type': 'application/json'
                },
                body: comment.id
            });
            if (!res.ok) return
            data.review.comments = data.review.comments.filter(c => c.id != comment.id);
        } catch (error) {
            console.error(error);
        }
    }

    async function deleteResponse(response, comment) {
        try {
            const res = await fetch(`/api/v1/deleteResponse?reviewId=${data.reviewId}`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get(),
                    'Content-Type': 'application/json'
                },
                body: response.id
            });
            if (!res.ok) return
            comment.responses = comment.responses.filter(r => r.id != response.id);
            data.review.comments = [...data.review.comments]
        } catch (error) {
            console.error(error);
        }
    }

    async function activateNotification() {
        try {
            const response = await fetch(`/api/v1/reviews/${data.reviewId}/notifications/activate`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get(),
                }
            });
            if (!response.ok) return
            data.notificationActivated = true;
        } catch (error) {
            console.error(error);
        }
    }

    async function deactivateNotification() {
        try {
            const response = await fetch(`/api/v1/reviews/${data.reviewId}/notifications/deactivate`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!response.ok) return
            data.notificationActivated = false;
        } catch (error) {
            console.error(error);
        }
    }

    async function likeReview() {
        try {
            const res = await fetch(`/api/v1/reviews/${data.review.id}/like`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!res.ok) return
            const body = await res.json()
            data.review.likes = body.likes
            data.review.likeState = body.likeState
        } catch (error) {
            console.error(error);
        }
    }

    async function dislikeReview() {
        try {
            const res = await fetch(`/api/v1/reviews/${data.review.id}/dislike`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!res.ok) return
            const body = await res.json()
            data.review.likes = body.likes
            data.review.likeState = body.likeState
        } catch (error) {
            console.error(error);
        }
    }

    async function likeComment(comment) {
        try {
            const res = await fetch(`/api/v1/comments/${comment.id}/like`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!res.ok) return
            const body = await res.json()
            comment.likes = body.likes
            comment.likeState = body.likeState
            data.review.comments = [...data.review.comments]
        } catch (error) {
            console.error(error);
        }
    }

    async function dislikeComment(comment) {
        try {
            const res = await fetch(`/api/v1/comments/${comment.id}/dislike`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!res.ok) return
            const body = await res.json()
            comment.likes = body.likes
            comment.likeState = body.likeState
            data.review.comments = [...data.review.comments]
        } catch (error) {
            console.error(error);
        }
    }

    async function likeResponse(response, comment) {
        try {
            const res = await fetch(`/api/v1/responses/${response.id}/like`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!res.ok) return
            const body = await res.json()
            console.log(body)
            response.likes = body.likes
            response.likeState = body.likeState
            comment.responses = [...comment.responses]
            data.review.comments = [...data.review.comments]
        } catch (error) {
            console.error(error);
        }
    }

    async function dislikeResponse(response, comment) {
        try {
            const res = await fetch(`/api/v1/responses/${response.id}/dislike`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!res.ok) return
            const body = await res.json()
            response.likes = body.likes
            response.likeState = body.likeState
            comment.responses = [...comment.responses]
            data.review.comments = [...data.review.comments]
        } catch (error) {
            console.error(error);
        }
    }

    async function fetchUnitTests() {
        let delay = 1000;

        while (!data.review.unitTests) {
            try {
                const response = await fetch(`/api/v1/reviews/${data.reviewId}/unitTests`);
                if (response.ok) {
                    const body = await response.json();
                    if (body.unitTests) {
                        data.review.unitTests = body.unitTests;
                        return;
                    }
                }
                await new Promise(resolve => setTimeout(resolve, delay));
                delay *= 2;
            } catch (error) {
                console.error(error);
                return;
            }
        }
    }

    fetchUnitTests()
</script>


<div class="container">
    <NavBar/>

    <div class="row">
        <div class="col-7">
            <div class="row">
                <h1 class="text-break text-justify text-wrap">{data.review.title}</h1>

                {#if isUserAdmin}
                    <form on:submit|preventDefault={deleteReview}>
                        <input type="hidden" name="id" value={data.reviewId} />
                        <button type="submit">
                            Delete
                        </button>
                    </form>
                {/if}
            </div>
        </div>
        <div class="col-2">
            {#if data.review.unitTests}
                {#if data.review.unitTests.errors.length != 0}
                    <div class="alert alert-danger d-flex justify-content-center align-items-center">Compilation Error</div>
                {:else}
                    <div class="alert d-flex justify-content-center align-items-center {data.review.unitTests.succeededCount == data.review.unitTests.totalCount ? 'alert-success' : 'alert-warning'}">
                        {data.review.unitTests.succeededCount} / {data.review.unitTests.totalCount}
                    </div>
                {/if}
            {:else}
                <div class="alert alert-info d-flex justify-content-center align-items-center">Test en cours</div>
            {/if}
        </div>
        <div class="col-2">
            <div class="row-6">
                <a href="/front/users/{data.review.author.id}">
                    {data.review.author.username}
                </a>
            </div>
            <span class="row-6">{formatDate(new Date(data.review.date))}</span>
        </div>
        <div class="col-1">
            {#if isAuthenticated}
                {#if !data.notificationActivated}
                    <form on:submit|preventDefault={activateNotification}>
                        <button type="submit">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bell" viewBox="0 0 16 16">
                                <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2M8 1.918l-.797.161A4 4 0 0 0 4 6c0 .628-.134 2.197-.459 3.742-.16.767-.376 1.566-.663 2.258h10.244c-.287-.692-.502-1.49-.663-2.258C12.134 8.197 12 6.628 12 6a4 4 0 0 0-3.203-3.92zM14.22 12c.223.447.481.801.78 1H1c.299-.199.557-.553.78-1C2.68 10.2 3 6.88 3 6c0-2.42 1.72-4.44 4.005-4.901a1 1 0 1 1 1.99 0A5 5 0 0 1 13 6c0 .88.32 4.2 1.22 6"/>
                            </svg>
                        </button>
                    </form>
                {:else}
                    <form on:submit|preventDefault={deactivateNotification}>
                        <button type="submit">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bell-fill" viewBox="0 0 16 16">
                                <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2m.995-14.901a1 1 0 1 0-1.99 0A5 5 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-2-5.902-2-7 0-2.42-1.72-4.44-4.005-4.901"/>
                            </svg>
                        </button>
                    </form>
                {/if}
            {/if}
        </div>
    </div>

    <div class="row mb-3">
        <div class="col-1 d-flex flex-column align-items-center">
            {#if isAuthenticated}
                <form on:submit|preventDefault={likeReview}>
                    <button type="submit">
                        {#if data.review.likeState != 'LIKE'}
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-up" viewBox="0 0 16 16">
                                <path d="M8.864.046C7.908-.193 7.02.53 6.956 1.466c-.072 1.051-.23 2.016-.428 2.59-.125.36-.479 1.013-1.04 1.639-.557.623-1.282 1.178-2.131 1.41C2.685 7.288 2 7.87 2 8.72v4.001c0 .845.682 1.464 1.448 1.545 1.07.114 1.564.415 2.068.723l.048.03c.272.165.578.348.97.484.397.136.861.217 1.466.217h3.5c.937 0 1.599-.477 1.934-1.064a1.86 1.86 0 0 0 .254-.912c0-.152-.023-.312-.077-.464.201-.263.38-.578.488-.901.11-.33.172-.762.004-1.149.069-.13.12-.269.159-.403.077-.27.113-.568.113-.857 0-.288-.036-.585-.113-.856a2 2 0 0 0-.138-.362 1.9 1.9 0 0 0 .234-1.734c-.206-.592-.682-1.1-1.2-1.272-.847-.282-1.803-.276-2.516-.211a10 10 0 0 0-.443.05 9.4 9.4 0 0 0-.062-4.509A1.38 1.38 0 0 0 9.125.111zM11.5 14.721H8c-.51 0-.863-.069-1.14-.164-.281-.097-.506-.228-.776-.393l-.04-.024c-.555-.339-1.198-.731-2.49-.868-.333-.036-.554-.29-.554-.55V8.72c0-.254.226-.543.62-.65 1.095-.3 1.977-.996 2.614-1.708.635-.71 1.064-1.475 1.238-1.978.243-.7.407-1.768.482-2.85.025-.362.36-.594.667-.518l.262.066c.16.04.258.143.288.255a8.34 8.34 0 0 1-.145 4.725.5.5 0 0 0 .595.644l.003-.001.014-.003.058-.014a9 9 0 0 1 1.036-.157c.663-.06 1.457-.054 2.11.164.175.058.45.3.57.65.107.308.087.67-.266 1.022l-.353.353.353.354c.043.043.105.141.154.315.048.167.075.37.075.581 0 .212-.027.414-.075.582-.05.174-.111.272-.154.315l-.353.353.353.354c.047.047.109.177.005.488a2.2 2.2 0 0 1-.505.805l-.353.353.353.354c.006.005.041.05.041.17a.9.9 0 0 1-.121.416c-.165.288-.503.56-1.066.56z"/>
                            </svg>
                        {/if}
                        {#if data.review.likeState === 'LIKE'}
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-up-fill" viewBox="0 0 16 16">
                                <path d="M6.956 1.745C7.021.81 7.908.087 8.864.325l.261.066c.463.116.874.456 1.012.965.22.816.533 2.511.062 4.51a10 10 0 0 1 .443-.051c.713-.065 1.669-.072 2.516.21.518.173.994.681 1.2 1.273.184.532.16 1.162-.234 1.733q.086.18.138.363c.077.27.113.567.113.856s-.036.586-.113.856c-.039.135-.09.273-.16.404.169.387.107.819-.003 1.148a3.2 3.2 0 0 1-.488.901c.054.152.076.312.076.465 0 .305-.089.625-.253.912C13.1 15.522 12.437 16 11.5 16H8c-.605 0-1.07-.081-1.466-.218a4.8 4.8 0 0 1-.97-.484l-.048-.03c-.504-.307-.999-.609-2.068-.722C2.682 14.464 2 13.846 2 13V9c0-.85.685-1.432 1.357-1.615.849-.232 1.574-.787 2.132-1.41.56-.627.914-1.28 1.039-1.639.199-.575.356-1.539.428-2.59z"/>
                            </svg>
                        {/if}
                    </button>
                </form>
            {/if}
            <div>
                {data.review.likes}
            </div>
            {#if isAuthenticated}
                <form on:submit|preventDefault={dislikeReview}>
                    <button type="submit">
                        {#if data.review.likeState != 'DISLIKE'}
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-down" viewBox="0 0 16 16">
                                <path d="M8.864 15.674c-.956.24-1.843-.484-1.908-1.42-.072-1.05-.23-2.015-.428-2.59-.125-.36-.479-1.012-1.04-1.638-.557-.624-1.282-1.179-2.131-1.41C2.685 8.432 2 7.85 2 7V3c0-.845.682-1.464 1.448-1.546 1.07-.113 1.564-.415 2.068-.723l.048-.029c.272-.166.578-.349.97-.484C6.931.08 7.395 0 8 0h3.5c.937 0 1.599.478 1.934 1.064.164.287.254.607.254.913 0 .152-.023.312-.077.464.201.262.38.577.488.9.11.33.172.762.004 1.15.069.13.12.268.159.403.077.27.113.567.113.856s-.036.586-.113.856c-.035.12-.08.244-.138.363.394.571.418 1.2.234 1.733-.206.592-.682 1.1-1.2 1.272-.847.283-1.803.276-2.516.211a10 10 0 0 1-.443-.05 9.36 9.36 0 0 1-.062 4.51c-.138.508-.55.848-1.012.964zM11.5 1H8c-.51 0-.863.068-1.14.163-.281.097-.506.229-.776.393l-.04.025c-.555.338-1.198.73-2.49.868-.333.035-.554.29-.554.55V7c0 .255.226.543.62.65 1.095.3 1.977.997 2.614 1.709.635.71 1.064 1.475 1.238 1.977.243.7.407 1.768.482 2.85.025.362.36.595.667.518l.262-.065c.16-.04.258-.144.288-.255a8.34 8.34 0 0 0-.145-4.726.5.5 0 0 1 .595-.643h.003l.014.004.058.013a9 9 0 0 0 1.036.157c.663.06 1.457.054 2.11-.163.175-.059.45-.301.57-.651.107-.308.087-.67-.266-1.021L12.793 7l.353-.354c.043-.042.105-.14.154-.315.048-.167.075-.37.075-.581s-.027-.414-.075-.581c-.05-.174-.111-.273-.154-.315l-.353-.354.353-.354c.047-.047.109-.176.005-.488a2.2 2.2 0 0 0-.505-.804l-.353-.354.353-.354c.006-.005.041-.05.041-.17a.9.9 0 0 0-.121-.415C12.4 1.272 12.063 1 11.5 1"/>
                            </svg>
                        {/if}
                        {#if data.review.likeState === 'DISLIKE'}
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-down-fill" viewBox="0 0 16 16">
                                <path d="M6.956 14.534c.065.936.952 1.659 1.908 1.42l.261-.065a1.38 1.38 0 0 0 1.012-.965c.22-.816.533-2.512.062-4.51q.205.03.443.051c.713.065 1.669.071 2.516-.211.518-.173.994-.68 1.2-1.272a1.9 1.9 0 0 0-.234-1.734c.058-.118.103-.242.138-.362.077-.27.113-.568.113-.856 0-.29-.036-.586-.113-.857a2 2 0 0 0-.16-.403c.169-.387.107-.82-.003-1.149a3.2 3.2 0 0 0-.488-.9c.054-.153.076-.313.076-.465a1.86 1.86 0 0 0-.253-.912C13.1.757 12.437.28 11.5.28H8c-.605 0-1.07.08-1.466.217a4.8 4.8 0 0 0-.97.485l-.048.029c-.504.308-.999.61-2.068.723C2.682 1.815 2 2.434 2 3.279v4c0 .851.685 1.433 1.357 1.616.849.232 1.574.787 2.132 1.41.56.626.914 1.28 1.039 1.638.199.575.356 1.54.428 2.591"/>
                            </svg>
                        {/if}
                    </button>
                </form>
            {/if}
        </div>
        <div class="col-11">
            <p class="row text-break text-justify text-wrap" style="white-space: pre-wrap;">
               {@html data.review.commentary} 
            </p>
            <div class="row form-group">
                <textarea readonly class="form-control" cols="86" rows="10" bind:this={editorId}>{data.review.code}</textarea>
            </div>
            <div class="row form-group">
                <textarea readonly class="form-control" cols="86" rows="10" bind:this={testEditorId}>{data.review.test}</textarea>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12">
            <ul class="list-group">
                {#if data.review.comments}
                    {#each data.review.comments as comment}
                        <li class="list-group-item" id="comment_{comment.id}">
                            <div class="row">
                                <div class="col-1 d-flex flex-column align-items-center">
                                    {#if isAuthenticated}
                                        <form on:submit|preventDefault={() => likeComment(comment)}>
                                            <button type="submit">
                                                {#if comment.likeState != 'LIKE'}
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-up" viewBox="0 0 16 16">
                                                        <path d="M8.864.046C7.908-.193 7.02.53 6.956 1.466c-.072 1.051-.23 2.016-.428 2.59-.125.36-.479 1.013-1.04 1.639-.557.623-1.282 1.178-2.131 1.41C2.685 7.288 2 7.87 2 8.72v4.001c0 .845.682 1.464 1.448 1.545 1.07.114 1.564.415 2.068.723l.048.03c.272.165.578.348.97.484.397.136.861.217 1.466.217h3.5c.937 0 1.599-.477 1.934-1.064a1.86 1.86 0 0 0 .254-.912c0-.152-.023-.312-.077-.464.201-.263.38-.578.488-.901.11-.33.172-.762.004-1.149.069-.13.12-.269.159-.403.077-.27.113-.568.113-.857 0-.288-.036-.585-.113-.856a2 2 0 0 0-.138-.362 1.9 1.9 0 0 0 .234-1.734c-.206-.592-.682-1.1-1.2-1.272-.847-.282-1.803-.276-2.516-.211a10 10 0 0 0-.443.05 9.4 9.4 0 0 0-.062-4.509A1.38 1.38 0 0 0 9.125.111zM11.5 14.721H8c-.51 0-.863-.069-1.14-.164-.281-.097-.506-.228-.776-.393l-.04-.024c-.555-.339-1.198-.731-2.49-.868-.333-.036-.554-.29-.554-.55V8.72c0-.254.226-.543.62-.65 1.095-.3 1.977-.996 2.614-1.708.635-.71 1.064-1.475 1.238-1.978.243-.7.407-1.768.482-2.85.025-.362.36-.594.667-.518l.262.066c.16.04.258.143.288.255a8.34 8.34 0 0 1-.145 4.725.5.5 0 0 0 .595.644l.003-.001.014-.003.058-.014a9 9 0 0 1 1.036-.157c.663-.06 1.457-.054 2.11.164.175.058.45.3.57.65.107.308.087.67-.266 1.022l-.353.353.353.354c.043.043.105.141.154.315.048.167.075.37.075.581 0 .212-.027.414-.075.582-.05.174-.111.272-.154.315l-.353.353.353.354c.047.047.109.177.005.488a2.2 2.2 0 0 1-.505.805l-.353.353.353.354c.006.005.041.05.041.17a.9.9 0 0 1-.121.416c-.165.288-.503.56-1.066.56z"/>
                                                    </svg>
                                                {/if}
                                                {#if comment.likeState === 'LIKE'}
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-up-fill" viewBox="0 0 16 16">
                                                        <path d="M6.956 1.745C7.021.81 7.908.087 8.864.325l.261.066c.463.116.874.456 1.012.965.22.816.533 2.511.062 4.51a10 10 0 0 1 .443-.051c.713-.065 1.669-.072 2.516.21.518.173.994.681 1.2 1.273.184.532.16 1.162-.234 1.733q.086.18.138.363c.077.27.113.567.113.856s-.036.586-.113.856c-.039.135-.09.273-.16.404.169.387.107.819-.003 1.148a3.2 3.2 0 0 1-.488.901c.054.152.076.312.076.465 0 .305-.089.625-.253.912C13.1 15.522 12.437 16 11.5 16H8c-.605 0-1.07-.081-1.466-.218a4.8 4.8 0 0 1-.97-.484l-.048-.03c-.504-.307-.999-.609-2.068-.722C2.682 14.464 2 13.846 2 13V9c0-.85.685-1.432 1.357-1.615.849-.232 1.574-.787 2.132-1.41.56-.627.914-1.28 1.039-1.639.199-.575.356-1.539.428-2.59z"/>
                                                    </svg>
                                                {/if}
                                            </button>
                                        </form>
                                    {/if}
                                    <div>
                                        {comment.likes}
                                    </div>
                                    {#if isAuthenticated}
                                        <form on:submit|preventDefault={() => dislikeComment(comment)}>
                                            <button type="submit">
                                                {#if comment.likeState != 'DISLIKE'}
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-down" viewBox="0 0 16 16">
                                                        <path d="M8.864 15.674c-.956.24-1.843-.484-1.908-1.42-.072-1.05-.23-2.015-.428-2.59-.125-.36-.479-1.012-1.04-1.638-.557-.624-1.282-1.179-2.131-1.41C2.685 8.432 2 7.85 2 7V3c0-.845.682-1.464 1.448-1.546 1.07-.113 1.564-.415 2.068-.723l.048-.029c.272-.166.578-.349.97-.484C6.931.08 7.395 0 8 0h3.5c.937 0 1.599.478 1.934 1.064.164.287.254.607.254.913 0 .152-.023.312-.077.464.201.262.38.577.488.9.11.33.172.762.004 1.15.069.13.12.268.159.403.077.27.113.567.113.856s-.036.586-.113.856c-.035.12-.08.244-.138.363.394.571.418 1.2.234 1.733-.206.592-.682 1.1-1.2 1.272-.847.283-1.803.276-2.516.211a10 10 0 0 1-.443-.05 9.36 9.36 0 0 1-.062 4.51c-.138.508-.55.848-1.012.964zM11.5 1H8c-.51 0-.863.068-1.14.163-.281.097-.506.229-.776.393l-.04.025c-.555.338-1.198.73-2.49.868-.333.035-.554.29-.554.55V7c0 .255.226.543.62.65 1.095.3 1.977.997 2.614 1.709.635.71 1.064 1.475 1.238 1.977.243.7.407 1.768.482 2.85.025.362.36.595.667.518l.262-.065c.16-.04.258-.144.288-.255a8.34 8.34 0 0 0-.145-4.726.5.5 0 0 1 .595-.643h.003l.014.004.058.013a9 9 0 0 0 1.036.157c.663.06 1.457.054 2.11-.163.175-.059.45-.301.57-.651.107-.308.087-.67-.266-1.021L12.793 7l.353-.354c.043-.042.105-.14.154-.315.048-.167.075-.37.075-.581s-.027-.414-.075-.581c-.05-.174-.111-.273-.154-.315l-.353-.354.353-.354c.047-.047.109-.176.005-.488a2.2 2.2 0 0 0-.505-.804l-.353-.354.353-.354c.006-.005.041-.05.041-.17a.9.9 0 0 0-.121-.415C12.4 1.272 12.063 1 11.5 1"/>
                                                    </svg>
                                                {/if}
                                                {#if comment.likeState === 'DISLIKE'}
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-down-fill" viewBox="0 0 16 16">
                                                        <path d="M6.956 14.534c.065.936.952 1.659 1.908 1.42l.261-.065a1.38 1.38 0 0 0 1.012-.965c.22-.816.533-2.512.062-4.51q.205.03.443.051c.713.065 1.669.071 2.516-.211.518-.173.994-.68 1.2-1.272a1.9 1.9 0 0 0-.234-1.734c.058-.118.103-.242.138-.362.077-.27.113-.568.113-.856 0-.29-.036-.586-.113-.857a2 2 0 0 0-.16-.403c.169-.387.107-.82-.003-1.149a3.2 3.2 0 0 0-.488-.9c.054-.153.076-.313.076-.465a1.86 1.86 0 0 0-.253-.912C13.1.757 12.437.28 11.5.28H8c-.605 0-1.07.08-1.466.217a4.8 4.8 0 0 0-.97.485l-.048.029c-.504.308-.999.61-2.068.723C2.682 1.815 2 2.434 2 3.279v4c0 .851.685 1.433 1.357 1.616.849.232 1.574.787 2.132 1.41.56.626.914 1.28 1.039 1.638.199.575.356 1.54.428 2.591"/>
                                                    </svg>
                                                {/if}
                                            </button>
                                        </form>
                                    {/if}
                                </div>
                                {#if isUserAdmin}
                                    <form on:submit|preventDefault={() => deleteComment(comment)} class="col-1">
                                        <input type="hidden" name="id" value={comment.id} />
                                        <input type="hidden" name="reviewId" value={data.review.id} />
                                        <button class="col-0">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>    
                                {/if}
                                <div class="col-11">
                                    <div class="row">
                                        <div class="col">
                                            <a href="/front/users/{comment.author.id}">
                                                {comment.author.username}
                                            </a>
                                        </div>
                                        <p class="col" style="text-align:end;">
                                            {formatDate(new Date(comment.date))}
                                        </p>
                                    </div>
                                    <div class="row">
                                        <p class="col-12 text-break text-justify text-wrap" style="white-space: pre-wrap;">
                                            {@html comment.content}
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <ul class="list-group my-3">
                                {#if comment.responses}
                                    {#each comment.responses as response}
                                        <li class="list-group-item" id="response_{response.id}">
                                            <div class="row">
                                                <div class="col-1 d-flex flex-column align-items-center">
                                                    {#if isAuthenticated}
                                                        <form on:submit|preventDefault={() => likeResponse(response, comment)}>
                                                            <button type="submit">
                                                                {#if response.likeState != 'LIKE'}
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-up" viewBox="0 0 16 16">
                                                                        <path d="M8.864.046C7.908-.193 7.02.53 6.956 1.466c-.072 1.051-.23 2.016-.428 2.59-.125.36-.479 1.013-1.04 1.639-.557.623-1.282 1.178-2.131 1.41C2.685 7.288 2 7.87 2 8.72v4.001c0 .845.682 1.464 1.448 1.545 1.07.114 1.564.415 2.068.723l.048.03c.272.165.578.348.97.484.397.136.861.217 1.466.217h3.5c.937 0 1.599-.477 1.934-1.064a1.86 1.86 0 0 0 .254-.912c0-.152-.023-.312-.077-.464.201-.263.38-.578.488-.901.11-.33.172-.762.004-1.149.069-.13.12-.269.159-.403.077-.27.113-.568.113-.857 0-.288-.036-.585-.113-.856a2 2 0 0 0-.138-.362 1.9 1.9 0 0 0 .234-1.734c-.206-.592-.682-1.1-1.2-1.272-.847-.282-1.803-.276-2.516-.211a10 10 0 0 0-.443.05 9.4 9.4 0 0 0-.062-4.509A1.38 1.38 0 0 0 9.125.111zM11.5 14.721H8c-.51 0-.863-.069-1.14-.164-.281-.097-.506-.228-.776-.393l-.04-.024c-.555-.339-1.198-.731-2.49-.868-.333-.036-.554-.29-.554-.55V8.72c0-.254.226-.543.62-.65 1.095-.3 1.977-.996 2.614-1.708.635-.71 1.064-1.475 1.238-1.978.243-.7.407-1.768.482-2.85.025-.362.36-.594.667-.518l.262.066c.16.04.258.143.288.255a8.34 8.34 0 0 1-.145 4.725.5.5 0 0 0 .595.644l.003-.001.014-.003.058-.014a9 9 0 0 1 1.036-.157c.663-.06 1.457-.054 2.11.164.175.058.45.3.57.65.107.308.087.67-.266 1.022l-.353.353.353.354c.043.043.105.141.154.315.048.167.075.37.075.581 0 .212-.027.414-.075.582-.05.174-.111.272-.154.315l-.353.353.353.354c.047.047.109.177.005.488a2.2 2.2 0 0 1-.505.805l-.353.353.353.354c.006.005.041.05.041.17a.9.9 0 0 1-.121.416c-.165.288-.503.56-1.066.56z"/>
                                                                    </svg>
                                                                {/if}
                                                                {#if response.likeState === 'LIKE'}
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-up-fill" viewBox="0 0 16 16">
                                                                        <path d="M6.956 1.745C7.021.81 7.908.087 8.864.325l.261.066c.463.116.874.456 1.012.965.22.816.533 2.511.062 4.51a10 10 0 0 1 .443-.051c.713-.065 1.669-.072 2.516.21.518.173.994.681 1.2 1.273.184.532.16 1.162-.234 1.733q.086.18.138.363c.077.27.113.567.113.856s-.036.586-.113.856c-.039.135-.09.273-.16.404.169.387.107.819-.003 1.148a3.2 3.2 0 0 1-.488.901c.054.152.076.312.076.465 0 .305-.089.625-.253.912C13.1 15.522 12.437 16 11.5 16H8c-.605 0-1.07-.081-1.466-.218a4.8 4.8 0 0 1-.97-.484l-.048-.03c-.504-.307-.999-.609-2.068-.722C2.682 14.464 2 13.846 2 13V9c0-.85.685-1.432 1.357-1.615.849-.232 1.574-.787 2.132-1.41.56-.627.914-1.28 1.039-1.639.199-.575.356-1.539.428-2.59z"/>
                                                                    </svg>
                                                                {/if}
                                                            </button>
                                                        </form>
                                                    {/if}
                                                    <div>
                                                        {response.likes}
                                                    </div>
                                                    {#if isAuthenticated}
                                                        <form on:submit|preventDefault={() => dislikeResponse(response, comment)}>
                                                            <button type="submit">
                                                                {#if response.likeState != 'DISLIKE'}
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-down" viewBox="0 0 16 16">
                                                                        <path d="M8.864 15.674c-.956.24-1.843-.484-1.908-1.42-.072-1.05-.23-2.015-.428-2.59-.125-.36-.479-1.012-1.04-1.638-.557-.624-1.282-1.179-2.131-1.41C2.685 8.432 2 7.85 2 7V3c0-.845.682-1.464 1.448-1.546 1.07-.113 1.564-.415 2.068-.723l.048-.029c.272-.166.578-.349.97-.484C6.931.08 7.395 0 8 0h3.5c.937 0 1.599.478 1.934 1.064.164.287.254.607.254.913 0 .152-.023.312-.077.464.201.262.38.577.488.9.11.33.172.762.004 1.15.069.13.12.268.159.403.077.27.113.567.113.856s-.036.586-.113.856c-.035.12-.08.244-.138.363.394.571.418 1.2.234 1.733-.206.592-.682 1.1-1.2 1.272-.847.283-1.803.276-2.516.211a10 10 0 0 1-.443-.05 9.36 9.36 0 0 1-.062 4.51c-.138.508-.55.848-1.012.964zM11.5 1H8c-.51 0-.863.068-1.14.163-.281.097-.506.229-.776.393l-.04.025c-.555.338-1.198.73-2.49.868-.333.035-.554.29-.554.55V7c0 .255.226.543.62.65 1.095.3 1.977.997 2.614 1.709.635.71 1.064 1.475 1.238 1.977.243.7.407 1.768.482 2.85.025.362.36.595.667.518l.262-.065c.16-.04.258-.144.288-.255a8.34 8.34 0 0 0-.145-4.726.5.5 0 0 1 .595-.643h.003l.014.004.058.013a9 9 0 0 0 1.036.157c.663.06 1.457.054 2.11-.163.175-.059.45-.301.57-.651.107-.308.087-.67-.266-1.021L12.793 7l.353-.354c.043-.042.105-.14.154-.315.048-.167.075-.37.075-.581s-.027-.414-.075-.581c-.05-.174-.111-.273-.154-.315l-.353-.354.353-.354c.047-.047.109-.176.005-.488a2.2 2.2 0 0 0-.505-.804l-.353-.354.353-.354c.006-.005.041-.05.041-.17a.9.9 0 0 0-.121-.415C12.4 1.272 12.063 1 11.5 1"/>
                                                                    </svg>
                                                                {/if}
                                                                {#if response.likeState === 'DISLIKE'}
                                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hand-thumbs-down-fill" viewBox="0 0 16 16">
                                                                        <path d="M6.956 14.534c.065.936.952 1.659 1.908 1.42l.261-.065a1.38 1.38 0 0 0 1.012-.965c.22-.816.533-2.512.062-4.51q.205.03.443.051c.713.065 1.669.071 2.516-.211.518-.173.994-.68 1.2-1.272a1.9 1.9 0 0 0-.234-1.734c.058-.118.103-.242.138-.362.077-.27.113-.568.113-.856 0-.29-.036-.586-.113-.857a2 2 0 0 0-.16-.403c.169-.387.107-.82-.003-1.149a3.2 3.2 0 0 0-.488-.9c.054-.153.076-.313.076-.465a1.86 1.86 0 0 0-.253-.912C13.1.757 12.437.28 11.5.28H8c-.605 0-1.07.08-1.466.217a4.8 4.8 0 0 0-.97.485l-.048.029c-.504.308-.999.61-2.068.723C2.682 1.815 2 2.434 2 3.279v4c0 .851.685 1.433 1.357 1.616.849.232 1.574.787 2.132 1.41.56.626.914 1.28 1.039 1.638.199.575.356 1.54.428 2.591"/>
                                                                    </svg>
                                                                {/if}
                                                            </button>
                                                        </form>
                                                    {/if}
                                                </div>
                                                {#if isUserAdmin}
                                                    <form on:submit|preventDefault={() => deleteResponse(response, comment)} class="col-1">
                                                        <input type="hidden" name="id" value={response.id} />
                                                        <input type="hidden" name="reviewId" value={data.review.id} />
                                                        <button>
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </form>
                                                {/if}
                                                <div class="col-11">
                                                    <div class="row">
                                                        <div class="col">
                                                            <a href="/front/users/{response.author.id}">
                                                                {response.author.username}
                                                            </a>
                                                        </div>
                                                        <p class="col" style="text-align:end;">
                                                            {formatDate(new Date(comment.date))}
                                                        </p>
                                                    </div>
                                                    <div class="row">
                                                        <p class="col-12 text-break text-justify text-wrap" style="white-space: pre-wrap;">
                                                            {@html response.content}
                                                        </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </li>
                                    {/each}
                                    {#if isAuthenticated}
                                       <ReplyForm reviewId={data.review.id} comment={comment} bind:responses={comment.responses}/>
                                    {/if}
                                {/if}
                            </ul>
                        </li>
                    {/each}
                {/if}
            </ul>
            {#if isAuthenticated}
                <div class="comment-form row form-group my-3">
                   <textarea id="comment" bind:value={commentValue} class="form-control"/>
                   <button type="submit" on:click={comment}>Commenter</button>
                </div>
            {/if}
        </div>
    </div>
</div>

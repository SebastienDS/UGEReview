<script>
import { goto } from '$app/navigation';
import { authToken } from '$lib/auth';
import NavBar from '$lib/components/NavBar.svelte';

const form = {
    title: '',
    commentary: '',
    code: '',
    test: ''
}
let codeFiles = [];
let testFiles = [];

async function createReview() {
    const formData = new FormData();
    for(const name in form) {
        formData.append(name, form[name]);
    }
    if (codeFiles.length != 0) formData.append("codeFile", codeFiles[0])
    if (testFiles.length != 0) formData.append("testFile", testFiles[0])
    
    try {
        const response = await fetch("/api/v1/createReview", {
            method: 'POST',
            headers: {
                'Authorization': authToken.get()
            },
            body: formData
        });
        const createdReview = await response.json()
        goto('/front/reviews/' + createdReview.id);
    } catch (error) {
        console.error(error);
    }
}
</script>

<div class="container">
    <NavBar/>

    <h1>UGERevue</h1>

    <form on:submit|preventDefault={createReview} class="mb-5">
        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-paragraph" viewBox="0 0 16 16">
                            <path d="M10.5 15a.5.5 0 0 1-.5-.5V2H9v12.5a.5.5 0 0 1-1 0V9H7a4 4 0 1 1 0-8h5.5a.5.5 0 0 1 0 1H11v12.5a.5.5 0 0 1-.5.5"/>
                        </svg>
                    </span>
                </div>
                <input type="text" class="form-control" id="title" placeholder="Titre :" bind:value={form.title}>
            </div>
        </div>
        <div class="form-group">
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-paragraph" viewBox="0 0 16 16">
                            <path d="M10.5 15a.5.5 0 0 1-.5-.5V2H9v12.5a.5.5 0 0 1-1 0V9H7a4 4 0 1 1 0-8h5.5a.5.5 0 0 1 0 1H11v12.5a.5.5 0 0 1-.5.5"/>
                        </svg>
                    </span>
                </div>
                <textarea class="form-control" placeholder="Commentaire :" cols="86" rows="2" bind:value={form.commentary}></textarea>
            </div>
        </div>
        

        <ul class="nav nav-tabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="code-tab" data-bs-toggle="tab" data-bs-target="#code" type="button" role="tab" aria-controls="code" aria-selected="true">Text</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="codeFile-tab" data-bs-toggle="tab" data-bs-target="#codeFile" type="button" role="tab" aria-controls="codeFile" aria-selected="false">File</button>
            </li>
        </ul>
        <div class="tab-content my-2">
            <div class="tab-pane fade show active" id="code" role="tabpanel" aria-labelledby="code-tab">
                <div class="form-group">
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-body-text" viewBox="0 0 16 16">
                                    <path fill-rule="evenodd" d="M0 .5A.5.5 0 0 1 .5 0h4a.5.5 0 0 1 0 1h-4A.5.5 0 0 1 0 .5m0 2A.5.5 0 0 1 .5 2h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m9 0a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m-9 2A.5.5 0 0 1 .5 4h3a.5.5 0 0 1 0 1h-3a.5.5 0 0 1-.5-.5m5 0a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m7 0a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 0 1h-3a.5.5 0 0 1-.5-.5m-12 2A.5.5 0 0 1 .5 6h6a.5.5 0 0 1 0 1h-6a.5.5 0 0 1-.5-.5m8 0a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m-8 2A.5.5 0 0 1 .5 8h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m7 0a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m-7 2a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 0 1h-8a.5.5 0 0 1-.5-.5m0 2a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5m0 2a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 0 1h-2a.5.5 0 0 1-.5-.5"/>
                                </svg>
                            </span>
                        </div>
                        <textarea name="code" class="form-control" placeholder="Code :" cols="86" rows="10" bind:value={form.code}></textarea>
                    </div>
                </div>
            </div>
            <div class="tab-pane fade" id="codeFile" role="tabpanel" aria-labelledby="codeFile-tab">
                <div class="form-group">
                    <div class="input-group mb-3">
                        <input type="file" class="form-control" id="inputCodeFile" name="codeFile" bind:files={codeFiles}>
                        <label class="input-group-text" for="inputCodeFile">Code</label>
                    </div>
                </div>
            </div>
        </div>


        <ul class="nav nav-tabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="test-tab" data-bs-toggle="tab" data-bs-target="#test" type="button" role="tab" aria-controls="test" aria-selected="true">Text</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="testFile-tab" data-bs-toggle="tab" data-bs-target="#testFile" type="button" role="tab" aria-controls="testFile" aria-selected="false">File</button>
            </li>
        </ul>
        <div class="tab-content my-2">
            <div class="tab-pane fade show active" id="test" role="tabpanel" aria-labelledby="test-tab">
                <div class="form-group">
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-body-text" viewBox="0 0 16 16">
                                    <path fill-rule="evenodd" d="M0 .5A.5.5 0 0 1 .5 0h4a.5.5 0 0 1 0 1h-4A.5.5 0 0 1 0 .5m0 2A.5.5 0 0 1 .5 2h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m9 0a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m-9 2A.5.5 0 0 1 .5 4h3a.5.5 0 0 1 0 1h-3a.5.5 0 0 1-.5-.5m5 0a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m7 0a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 0 1h-3a.5.5 0 0 1-.5-.5m-12 2A.5.5 0 0 1 .5 6h6a.5.5 0 0 1 0 1h-6a.5.5 0 0 1-.5-.5m8 0a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m-8 2A.5.5 0 0 1 .5 8h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5m7 0a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m-7 2a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 0 1h-8a.5.5 0 0 1-.5-.5m0 2a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5m0 2a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 0 1h-2a.5.5 0 0 1-.5-.5"/>
                                </svg>
                            </span>
                        </div>
                        <textarea name="test" class="form-control" placeholder="Test :" cols="86" rows="10" bind:value={form.test}></textarea>
                    </div>
                </div>
            </div>
            <div class="tab-pane fade" id="testFile" role="tabpanel" aria-labelledby="testFile-tab">
                <div class="form-group">
                    <div class="input-group mb-3">
                        <input type="file" class="form-control" id="inputTestFile" name="testFile" bind:files={testFiles}>
                        <label class="input-group-text" for="inputTestFile">Test</label>
                    </div>
                </div>
            </div>
        </div>


        <div class="form-group mb-3">
            <button type="submit" class="btn btn-primary">Créer une review</button>
        </div>
    </form>
</div>
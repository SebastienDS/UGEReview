<script>
    import NavBar from '$lib/components/NavBar.svelte';
    import { authToken } from '$lib/auth';

    export let data;

    async function followFunction() {
        try {
            const url = data.checkFollow ? `/api/v1/users/${data.user.id}/unfollow` : `/api/v1/users/${data.user.id}/follow`;
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                },
            });
            if (!response.ok) {
                return
            }
            data.checkFollow = !data.checkFollow;
        } catch (error) {
            console.error(error);
        }
    }

    function edit(normalRow, editRow, editIcon, validateIcon, url, input, field, span) {
        editIcon.addEventListener('click', function () {
                normalRow.style.display = 'none';
                editRow.style.display = 'block';
        });

        validateIcon.addEventListener('click', function () {
                normalRow.style.display = 'block';
                editRow.style.display = 'none';
                const newData = input.value
                fetch(url, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: newData
                })
                .then(response => {
                    if(response.ok){
                        span.textContent = input.value
                        console.log("OK");
                    } else {
                        input.value = span.textContent
                        console.log(response);
                    }
                })
                .catch(error => {
                    console.error('Erreur lors de la requête PUT :', error);
                });
            });
    }

    function editUser(){
        const editIconUser = document.getElementById('editIconUsername');
        const normalUsername = document.getElementById('rowNormalUsername');
        const editUsername = document.getElementById('rowEditUsername');
        const span = document.getElementById('username');
        const input = document.getElementById('usernameInput');
        const validateIconUser = document.getElementById('validateIconUsername');
        const url = window.location.href + "/updateUsername";
        edit(normalUsername, editUsername, editIconUser, validateIconUser, url, input, 'username', span);
    }

    function editEmail(){
        const editIconEmail = document.getElementById('editIconEmail');
        const normalEmailRow = document.getElementById('rowNormalEmail');
        const editEmailRow = document.getElementById('rowEditEmail');
        const span = document.getElementById('email');
        const input = document.getElementById('emailInput');
        const validateIconEmail = document.getElementById('validateIconEmail');
        const url = window.location.href + "/updateEmail";
        edit(normalEmailRow, editEmailRow, editIconEmail, validateIconEmail, url, input, 'email', span);
    }

    function manageEditPassword(normalRow, editRow, editIcon, validateIcon, url, inputOldPassword, inputNewPassword, span){
        editIcon.addEventListener('click', function () {
                normalRow.style.display = 'none';
                editRow.style.display = 'block';
        });

        validateIcon.addEventListener('click', function () {
                normalRow.style.display = 'block';
                editRow.style.display = 'none';
                const newData = JSON.stringify({ oldPassword: inputOldPassword.value, newPassword: inputNewPassword.value })
                fetch(url, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: newData
                })
                .then(response => {
                    if(response.ok){
                        console.log("OK");
                    } else {
                        console.log(response);
                    }
                    inputOldPassword.value = ""
                    inputNewPassword.value = ""
                })
                .catch(error => {
                    console.error('Erreur lors de la requête PUT :', error);
                });
            });
    }

    function editPassword(){
        const editIconPassword = document.getElementById('editIconPassword');
        const normalPasswordRow = document.getElementById('rowNormalPassword');
        const editPasswordRow = document.getElementById('rowEditPassword');
        const span = document.getElementById('password');
        const inputOldPassword = document.getElementById('oldPasswordInput');
        const inputNewPassword = document.getElementById('newPasswordInput');
        const validateIconPassword = document.getElementById('validateIconPassword');
        const url = window.location.href + "/updatePassword";
        manageEditPassword(normalPasswordRow, editPasswordRow, editIconPassword, validateIconPassword, url, inputOldPassword, inputNewPassword, span);
    }

    document.addEventListener('DOMContentLoaded', function () {
        if(data.isMyUserPage){
            editUser();
            editEmail();
            editPassword();
        }
    });

</script>
<NavBar/>

{#if data.isAuthenticated && data.isMyUserPage}
    <div>
        <form on:submit|preventDefault={followFunction} class="mb-5">
            <div class="form-group mb-3">
                {#if data.checkFollow}
                    <button type="submit" class="btn btn-primary">Unfollow</button>
                {:else}
                    <button type="submit" class="btn btn-primary">Follow</button>
                {/if}
            </div>
        </form>
    </div>
{/if}

<div class="container text-center">
    <div class="col">
        <div class="row">
            <a href="/front/users/{data.userId}/reviews">Revues</a>
        </div>
        <div class="row">
            <a href="/front/users/{data.userId}/comments">Commentaires</a>
        </div>
        <div class="row">
            <a href="/front/users/{data.userId}/responses">Réponses</a>
        </div>
        <div class="row">
            <a href="/front/users/{data.userId}/likes">Likes</a>
        </div>
    </div>
</div>
<div class="container">
    <div class="row">
        <div class="col text-end">
            <p>image</p>
        </div>
        <div class="col">
            <div id="rowNormalUsername" class="row">
                <div>
                    <span id="username" class="me-2">{data.user.username}</span>
                        {#if data.isMyUserPage}
                            <div>
                                <i id="editIconUsername" class="fas fa-edit"></i>
                            </div>
                        {/if}
                </div>
            </div>
            <div id="rowEditUsername" class="row" style="display: none">
                <div class="col">
                    <input id="usernameInput" maxlength="30" style="width: 250px;" class="form-control me-2" value={data.user.username}> 
                </div>
                <div class = "col">
                    <i id="validateIconUsername" class="fas fa-check"></i>
                </div>
            </div>
            {#if data.isMyUserPage}
                <div class="row">
                    <div id="rowNormalEmail" class="row">
                        <p>
                            <span id="email" class="me-2">{data.user.email}</span>
                            <i id="editIconEmail" class="fas fa-edit"></i>
                        </p>
                    </div>
                    <div id="rowEditEmail" class="row" style="display: none">
                        <div class="col">
                            <input id="emailInput" maxlength="30" style="width: 250px;" class="form-control me-2" value={data.user.email}>
                        </div>
                        <div class = "col">
                            <i id="validateIconEmail" class="fas fa-check"></i>
                        </div>
                    </div>
                </div>
            {/if}
            <div class="row">
                <span>{data.user.dateCreation}</span>
            </div>
            {#if data.isMyUserPage}
                <div class="row">
                    <div id="rowNormalPassword" class="row">
                        <p>
                            <span id="password" class="me-2">********</span>
                            <i id="editIconPassword" class="fas fa-edit"></i>
                        </p>
                    </div>
                    <div id="rowEditPassword" class="row" style="display: none">
                        <div class="col">
                            <input id="oldPasswordInput" placeholder="Old Password" maxlength="30" style="width: 250px;" class="form-control me-2">
                            <input id="newPasswordInput" placeholder="New Password" maxlength="30" style="width: 250px;" class="form-control me-2">
                        </div>
                        <div class = "col">
                            <i id="validateIconPassword" class="fas fa-check"></i>
                        </div>
                    </div>
                    {#if data.isAuthenticated && data.isMyUserPage}
                        <div class="mb-3">
                            <a href="/front/logout">Se déconnecter</a>
                        </div>
                    {/if}
                    <!-- <div th:if="${authenticated != null && isMyUserPage != null}">
                        <form th:action="@{/deleteProfile}" method="post">
                            <button type="submit">Supprimer mon compte</button>
                        </form>
                    </div> -->
                </div>
            {/if}
            <!-- <div th:if="${isUserPageAdmin == null && isUserAdmin != null}">
                <form th:action="@{/banProfile}" method="post">
                    <input type="hidden" name="id" th:value="${userId}" />
                    <button type="submit">Bannir le compte</button>
                </form>
            </div> -->
        </div>
    </div>
</div>

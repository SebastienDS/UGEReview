<script>
    import Header from '$lib/components/Header.svelte';

    export let data;
</script>
<div class="container">
    <Header/>
    <h2>Erreur</h2>
    <h2> {data.errorId}</h2>
</div>


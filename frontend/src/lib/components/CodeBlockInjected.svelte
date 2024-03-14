<script>
    import { onMount } from 'svelte';

    export let content

    let container

    onMount(() => {
        container.innerHTML = container.innerHTML.replaceAll(/```([^`]*)```/g, "<textarea>$1</textarea>")

        const textareas = container.querySelectorAll("textarea")
        textareas.forEach(block => {
            const editor = CodeMirror.fromTextArea(block, {
                mode:"text/x-java",
                theme: "dracula",
                lineNumbers: true,
                readOnly: true
            });
            editor.setSize(null, "auto")
        })
    })
</script>

<p class="text-break text-justify" style="white-space: pre-wrap;" bind:this={container}>
    {@html content}
</p>
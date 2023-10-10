import {SERVER_URL} from "../constant";

function CarForm({ handleClick }) {

    function handleSubmit(event) {
        event.preventDefault();

        const formData = new FormData(event.target);
        const formJson = Object.fromEntries(formData.entries());

        const requestOptions = {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(formJson)
        }

        fetch (
            SERVER_URL + '/car',
            requestOptions
        ).then(async response => {
            if(!response.ok) {
                console.log("error creating car")
            }
            handleClick();
        })

    }
return (
    <form onSubmit={handleSubmit}>
        <input name="brand" placeholder={"Enter Car Brand Name"}/>
        <input name="model" placeholder={"Enter Car Model Name"}/>
        <input name="year" placeholder={"Enter Car Year"}/>
        <button type={"submit"}>Add Car</button>
    </form>
    )
}


export default CarForm
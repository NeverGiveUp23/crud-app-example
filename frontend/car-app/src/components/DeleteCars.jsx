import {SERVER_URL} from "../constant";

function DeleteCars({id, handleClick}) {


    function deleteCar() {
        const requestOptions = {
            method: "DELETE",
            headers: {'Content-Type': 'application/json'}
        }
        let idObject = {id}
        fetch (
            SERVER_URL + '/car/' + idObject.id,
            requestOptions
        ).then(async response => {
            if(!response.ok) {
                console.log("error creating car")
            }
            handleClick();
        })
    }


    return (
        <button onClick={deleteCar} >Delete</button>
    )
}

export default DeleteCars;
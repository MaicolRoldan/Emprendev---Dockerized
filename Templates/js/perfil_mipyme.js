const PersonalInformationBtn = document.getElementById("datos-personales-btn");
const DescriptionBtn = document.getElementById("descripcion-btn");
const TituloInfo = document.getElementById("tituloinfo");

const DescriptionInformation = document.getElementById("descripcion-informacion");
const DatosPeronales = document.getElementById("datos-personales");

const userSubMenu = document.getElementById("user-sub-menu");

const TasksContainer = document.getElementById("task-container");

const File = document.getElementById("foto");
const DefaultFile = "img/Gato1.jpg";
const UserImgPerfil = document.getElementById("user-img-perfil");


function ButtonDataSelected(){
    PersonalInformationBtn.style.display="";
    PersonalInformationBtn.style.backgroundColor="gray";
    PersonalInformationBtn.style.transitionDuration="0.5s";
    PersonalInformationBtn.style.color="white";  
    TituloInfo.textContent="Informacion General";

    DescriptionBtn.style.display="";
    DescriptionBtn.style.backgroundColor="white";
    DescriptionBtn.style.color="black";

    DescriptionInformation.style.display="none";
    DatosPeronales.style.display="block";
}

//Submenu del perfil del usuario (imagen arriba a la izquierda)

userSubMenu.style.display = "none";

SubmenuPerfilBtn_isShowing = false;
function OpenPerfilSubMenu(){
    if (!SubmenuPerfilBtn_isShowing){
        userSubMenu.style.display="block";
    }
    else{
        userSubMenu.style.display="none";
    }
    SubmenuPerfilBtn_isShowing = !SubmenuPerfilBtn_isShowing;
};



//Desde aqui incia el script del cambio de foto de perfil del usuario
File.addEventListener('change', e => {
    if(e.target.files[0]){
        const reader = new FileReader();
        reader.onload = function(e){
        UserImgPerfil.src=e.target.result;
    }
    reader.readAsDataURL(e.target.files[0]);
    }else{
        UserImgPerfil = DefaultFile;
    }
});

async function checkSessionStatus() {
    try {
        const data = await $.ajax({
            url: "http://localhost:8080/emprendev/v1/user/sessionStatus",
            type: "GET",
            xhrFields: {
                withCredentials: true,
            },
        });

        if (data.sessionActive) {
            const id = data.userId;

            // Solicitar datos adicionales de Dev
            const mipymeData = await $.ajax({
                url: `http://localhost:8080/api/mipymes/${id}`,
                type: "GET",
                xhrFields: {
                    withCredentials: true,
                },
            });

            // Verifica los datos que recibes
            console.log("Datos de Mipyme:", mipymeData);

            // Actualiza el DOM con los datos de Dev
            $(".nameBusiness").text(mipymeData.nameBusiness) || "Descripción del negocio";
            $(".cityBusiness").text(mipymeData.cityBusiness);
            $(".addressBusiness").text(mipymeData.addressBusiness);
            $(".descriptionBusiness").text(mipymeData.descriptionBusiness);

        } else {
            console.log("No active session:", data.message);
        }
    } catch (error) {
        console.error("Error checking session status:", error);
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se pudo verificar el estado de la sesión o cargar los datos del mipyme.",
        });
    }
}

// Llama a la función para verificar el estado de la sesión
checkSessionStatus();
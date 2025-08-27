package com.gca.facade;

import com.gca.dto.PasswordChangeDTO;
import com.gca.dto.auth.AuthTokensDTO;
import com.gca.dto.auth.AuthenticationRequestDTO;
import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.TraineeTrainersUpdateDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.trainee.TraineeUpdateResponseDTO;
import com.gca.dto.trainer.TrainerCreateDTO;
import com.gca.dto.trainer.TrainerUpdateRequestDTO;
import com.gca.dto.trainer.TrainerUpdateResponseDTO;
import com.gca.dto.training.TrainingCreateDTO;
import com.gca.dto.training.TrainingDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.exception.TokenRefreshException;
import com.gca.mapper.rest.RestTraineeMapper;
import com.gca.mapper.rest.RestTrainerMapper;
import com.gca.mapper.rest.RestTrainingMapper;
import com.gca.model.TrainingType;
import com.gca.openapi.model.AssignedTrainerResponse;
import com.gca.openapi.model.LoginChangeRequest;
import com.gca.openapi.model.LoginRequest;
import com.gca.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gca.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gca.openapi.model.TraineeCreateRequest;
import com.gca.openapi.model.TraineeCreateResponse;
import com.gca.openapi.model.TraineeGetResponse;
import com.gca.openapi.model.TraineeUpdateRequest;
import com.gca.openapi.model.TraineeUpdateResponse;
import com.gca.openapi.model.TrainerCreateRequest;
import com.gca.openapi.model.TrainerCreateResponse;
import com.gca.openapi.model.TrainerGetResponse;
import com.gca.openapi.model.TrainerUpdateRequest;
import com.gca.openapi.model.TrainerUpdateResponse;
import com.gca.openapi.model.TrainingCreateRequest;
import com.gca.openapi.model.TrainingGetResponse;
import com.gca.openapi.model.TrainingTypeResponse;
import com.gca.security.AuthenticationService;
import com.gca.security.jwt.JwtCookieService;
import com.gca.service.TraineeService;
import com.gca.service.TrainerService;
import com.gca.service.TrainingService;
import com.gca.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingAppFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final JwtCookieService jwtCookieService;

    private final RestTraineeMapper restTraineeMapper;
    private final RestTrainerMapper restTrainerMapper;
    private final RestTrainingMapper restTrainingMapper;

    public void login(LoginRequest loginRequest, HttpServletResponse response) {
        AuthenticationRequestDTO request =
                new AuthenticationRequestDTO(loginRequest.getUsername(), loginRequest.getPassword());

        AuthTokensDTO tokens = authenticationService.authenticate(request);
        addTokensToResponse(tokens, response);
    }

    public void logout(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authenticationService.logout(authentication.getName());

        cleanTokensFromResponse(response);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtCookieService.extractRefreshTokenFromCookies(request);

        if (refreshToken == null) {
            throw new TokenRefreshException("Refresh token is absent");
        }

        AuthTokensDTO tokens = authenticationService.refreshToken(refreshToken);
        addTokensToResponse(tokens, response);
    }

    public TraineeCreateResponse createTrainee(TraineeCreateRequest request) {
        TraineeCreateDTO traineeCreateDTO = restTraineeMapper.toDto(request);
        UserCredentialsDTO userCredentialsDTO = traineeService.createTrainee(traineeCreateDTO);

        return restTraineeMapper.toRest(userCredentialsDTO);
    }

    public TraineeUpdateResponse updateTrainee(String username, TraineeUpdateRequest request) {
        TraineeUpdateRequestDTO requestDTO = restTraineeMapper.toDto(username, request);
        TraineeUpdateResponseDTO responseDTO = traineeService.updateTrainee(requestDTO);

        return restTraineeMapper.toRest(responseDTO);
    }

    public TrainerCreateResponse createTrainer(TrainerCreateRequest request) {
        TrainerCreateDTO createDTO = restTrainerMapper.toDto(request);
        UserCredentialsDTO credentialsDTO = trainerService.createTrainer(createDTO);

        return restTrainerMapper.toRest(credentialsDTO);
    }

    public TrainerUpdateResponse updateTrainer(String username, TrainerUpdateRequest trainer) {
        TrainerUpdateRequestDTO requestDTO = restTrainerMapper.toDto(username, trainer);
        TrainerUpdateResponseDTO responseDTO = trainerService.updateTrainer(requestDTO);

        return restTrainerMapper.toRest(responseDTO);
    }

    public void createTraining(TrainingCreateRequest request) {
        TrainingCreateDTO dto = restTrainingMapper.toDto(request);
        trainingService.createTraining(dto);
    }

    public void changePassword(LoginChangeRequest request) {
        PasswordChangeDTO dto = new PasswordChangeDTO(request.getUsername(),
                request.getOldPassword(), request.getNewPassword());

        userService.changeUserPassword(dto);
    }

    public List<TrainingGetResponse> findFilteredTrainings(TrainingTrainerCriteriaFilter filter) {
        List<TrainingDTO> dtoList = trainingService.getTrainerTrainings(filter);

        return dtoList.stream()
                .map(restTrainingMapper::toRest)
                .toList();
    }

    public List<TrainingGetResponse> findFilteredTrainings(TrainingTraineeCriteriaFilter filter) {
        List<TrainingDTO> dtoList = trainingService.getTraineeTrainings(filter);

        return dtoList.stream()
                .map(restTrainingMapper::toRest)
                .toList();
    }

    public TraineeGetResponse getTraineeByUsername(String username) {
        return restTraineeMapper.toRest(traineeService.getTraineeByUsername(username));
    }

    public TrainerGetResponse getTrainerByUsername(String username) {
        return restTrainerMapper.toRest(trainerService.getTrainerByUsername(username));
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    public void toggleUserActiveStatus(String username, boolean isActive) {
        userService.toggleActiveStatus(username, isActive);
    }

    public List<AssignedTrainerResponse> getUnassignedTrainers(String traineeUsername) {
        return trainerService.getUnassignedTrainers(traineeUsername).stream()
                .map(restTrainerMapper::toRest)
                .toList();
    }

    public TraineeAssignedTrainersUpdateResponse updateTraineeTrainers(String username,
                                                                       TraineeAssignedTrainersUpdateRequest request) {
        TraineeTrainersUpdateDTO traineeTrainersRequest
                = new TraineeTrainersUpdateDTO(username, request.getTrainerUsernames());

        List<AssignedTrainerResponse> trainerList =
                traineeService.updateTraineeTrainers(traineeTrainersRequest).stream()
                        .map(restTrainerMapper::toRest)
                        .toList();

        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse();
        response.setTrainers(trainerList);

        return response;
    }

    public List<TrainingTypeResponse> getAllTrainingTypes() {
        List<TrainingType> typeList = trainingService.getAllTrainingTypes();

        return typeList.stream()
                .map(restTrainingMapper::toRest)
                .toList();
    }

    private void addTokensToResponse(AuthTokensDTO tokens, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                jwtCookieService.createAccessTokenCookie(tokens.getAccessToken()).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                jwtCookieService.createRefreshTokenCookie(tokens.getRefreshToken()).toString());
    }

    private void cleanTokensFromResponse(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                jwtCookieService.createCleanJwtCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                jwtCookieService.createCleanRefreshTokenCookie().toString());
    }
}



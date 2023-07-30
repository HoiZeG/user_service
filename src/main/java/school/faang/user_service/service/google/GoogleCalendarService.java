package school.faang.user_service.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoogleEventResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.util.google.JpaDataStoreFactory;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Corporation-X";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String CALENDAR_ID = "primary";
    private static final String REDIRECT_URI = "http://localhost:8080/api/v1/google/calendar/Callback";
    private final GoogleTokenRepository googleTokenRepository;
    private final EventService eventService;
    private final UserService userService;

    public GoogleEventResponseDto createEvent(Long userId, Long eventId) throws GeneralSecurityException, IOException {
        Event event = eventService.getEvent(eventId);
        User user = userService.getUser(userId);

        if (!user.getParticipatedEvents().contains(event)) {
            throw new DataValidationException("User with id " + userId + " is not part of event with id " + eventId);
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        if (!googleTokenRepository.existsByUser(user)) {
            return GoogleEventResponseDto.builder()
                    .message("Follow the link to authorize your calendar")
                    .link(getAuthorizationLink(HTTP_TRANSPORT, userId, eventId))
                    .build();
        }

        // Loads credential from store by User id
        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(HTTP_TRANSPORT, clientSecrets);
        Credential credential = flow.loadCredential(String.valueOf(userId));
        Calendar service = getService(HTTP_TRANSPORT, credential);

        // Inits and inserts event into Google Calendar
        com.google.api.services.calendar.model.Event googleEvent = mapToGoogleEvent(event, service);
        googleEvent = service.events().insert(CALENDAR_ID, googleEvent).execute();

        return getResponse(googleEvent.getHtmlLink());
    }

    private String getAuthorizationLink(NetHttpTransport HTTP_TRANSPORT, Long userId, Long eventId) throws IOException {
        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(HTTP_TRANSPORT, clientSecrets);

        // Returns authorization url
        return flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .setState(userId + "-" + eventId)
                .build();
    }

    public GoogleEventResponseDto handleCallback(String code, String userId, String eventId) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow flow = getFlow(HTTP_TRANSPORT, clientSecrets);

        // Creates and saves new user's credential from callback response authorization code
        TokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri(REDIRECT_URI)
                .execute();
        Credential credential = flow.createAndStoreCredential(response, userId);
        Calendar service = getService(HTTP_TRANSPORT, credential);

        // Здесь я создаю и добавляю новое событие в гугл календарь и возвращаю ссылку
        // Но не уверен стоит ли делать это в коллбэк методе
        Event event = eventService.getEvent(Long.parseLong(eventId));
        com.google.api.services.calendar.model.Event googleEvent = mapToGoogleEvent(event, service);
        googleEvent = service.events().insert(CALENDAR_ID, googleEvent).execute();

        return getResponse(googleEvent.getHtmlLink());
    }

    private com.google.api.services.calendar.model.Event mapToGoogleEvent(
            Event event, Calendar service) throws IOException {

        com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event()
                .setSummary(event.getTitle())
                .setDescription(event.getDescription())
                .setLocation(event.getLocation());

        // Gets actual timezone from user's Google calendar
        String userTimeZone = service.calendars().get(CALENDAR_ID).execute().getTimeZone();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        ZonedDateTime zonedStart = ZonedDateTime.of(event.getStartDate(), ZoneId.of(userTimeZone));
        ZonedDateTime zonedEnd = ZonedDateTime.of(event.getEndDate(), ZoneId.of(userTimeZone));

        DateTime startDateTime = new DateTime(zonedStart.format(formatter));
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime);
        googleEvent.setStart(start);

        DateTime endDateTime = new DateTime(zonedEnd.format(formatter));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime);
        googleEvent.setEnd(end);
        return googleEvent;
    }

    private Calendar getService(HttpTransport HTTP_TRANSPORT, Credential credential) {
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("File not found: " + CREDENTIALS_FILE_PATH);
        }
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    private GoogleAuthorizationCodeFlow getFlow(HttpTransport HTTP_TRANSPORT, GoogleClientSecrets clientSecrets)
            throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType("offline")
                .build();
    }

    private GoogleEventResponseDto getResponse(String link) {
        return GoogleEventResponseDto.builder()
                .message("The event has been successfully added to your Google calendar.")
                .link(link)
                .build();
    }
}

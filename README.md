# В данном микросервисе разработал:

1) Event Service для создания/обновления/удаления Ивента и сохранения в бд: https://github.com/HoiZeG/user_service/blob/phoenix-master-stream6/src/main/java/school/faang/user_service/service/event/EventServiceImpl.java
2) Event Controller без REST API(не предусмотрено): https://github.com/HoiZeG/user_service/blob/phoenix-master-stream6/src/main/java/school/faang/user_service/controller/event/EventController.java
3) EventStartEventPublisher(Redis) - для отправки бизнес-нотификаций: https://github.com/HoiZeG/user_service/blob/phoenix-master-stream6/src/main/java/school/faang/user_service/publisher/EventStartEventPublisher.java
4) FollowEventPublisher(Redis) - для отправки бизнес-нотификацийЖ https://github.com/HoiZeG/user_service/blob/phoenix-master-stream6/src/main/java/school/faang/user_service/publisher/FollowEventPublisher.java
5) Event mapper(MapStruct) для перевода дто в энтити и наоборот: https://github.com/HoiZeG/user_service/blob/phoenix-master-stream6/src/main/java/school/faang/user_service/mapper/EventMapper.java
6) Unit Tests(JUnit, Mockito) - тест контроллера и сервиса: https://github.com/HoiZeG/user_service/blob/phoenix-master-stream6/src/test/java/school/faang/user_service/service/event/EventServiceTest.java | https://github.com/HoiZeG/user_service/blob/phoenix-master-stream6/src/test/java/school/faang/user_service/controller/event/EventControllerTest.java

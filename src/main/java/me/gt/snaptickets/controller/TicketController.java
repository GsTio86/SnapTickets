package me.gt.snaptickets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.gt.snaptickets.dto.TicketDto;
import me.gt.snaptickets.model.Ticket;
import me.gt.snaptickets.service.ImageService;
import me.gt.snaptickets.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@CrossOrigin
@RestController
@Tag(name = "票券 API", description = "管理票券的操作")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ImageService imageService;

    @Operation(summary = "獲取票券資料")
    @GetMapping("/ticket/info/{id}")
    public ResponseEntity<Object> getTicketById(@PathVariable String id) {
        Ticket ticket = ticketService.getByTicketId(id);
        if (ticket == null) {
            return ResponseEntity.badRequest().body("查無此票券");
        }
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "獲取所有票券")
    @GetMapping("/ticket/all")
    public ResponseEntity<Object> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @Operation(summary = "圖片網址")
    @GetMapping(value = "/ticket/{id}/image.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        Ticket ticket = ticketService.getByTicketId(id);
        if (ticket == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(ticket.getImage());
    }

    @Operation(summary = "圖片網址(指定寬高)")
    @GetMapping(value = "/ticket/{id}/{width}x{height}/image.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String id, @PathVariable int width, @PathVariable int height) {
        Ticket ticket = ticketService.getByTicketId(id);
        if (ticket == null) {
            return ResponseEntity.badRequest().body(null);
        }
        byte[] image = ticket.getImage();
        if (image == null) {
            return ResponseEntity.badRequest().body(null);
        }
        byte[] resizedImage = imageService.resizeImage(image, width, height);
        return ResponseEntity.ok().body(resizedImage);
    }

    @Operation(summary = "建立票券")
    @PostMapping(value = "/admin/ticket/create")
    public ResponseEntity<Object> createTicket(@RequestBody TicketDto ticketDto) {
        try {
            Ticket ticket = ticketDto.convertToTicket();
            ticketService.createTicket(ticket);
            return ResponseEntity.ok().body(Map.of( "ticketId", ticket.getTicketId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("建立票券失敗");
        }
    }

    @Operation(summary = "上傳票券圖片")
    @PostMapping(value = "/admin/ticket/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@PathVariable String id, @RequestPart MultipartFile image) {
        try {
            TicketService.ActionStatus status = ticketService.updateImage(id, image.getBytes());
            if (status == TicketService.ActionStatus.UPDATE_FAILED) {
                return ResponseEntity.badRequest().body("圖片上傳失敗");
            }
            return ResponseEntity.ok("圖片上傳成功");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("圖片上傳失敗");
        }
    }

    @Operation(summary = "更新票券")
    @PutMapping( "/admin/ticket/update/{id}")
    public ResponseEntity<String> updateTicket(@PathVariable String id, @RequestBody TicketDto ticketDto) {
        TicketService.ActionStatus status = ticketService.updateTicket(ticketDto.convertToTicket(id));
        if (status == TicketService.ActionStatus.UPDATE_FAILED) {
            return ResponseEntity.badRequest().body("票券更新失敗");
        }
        return ResponseEntity.ok("票券已更新");
    }

    @Operation(summary = "刪除票券")
    @DeleteMapping("/admin/ticket/delete/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable String id) {
        TicketService.ActionStatus status = ticketService.deleteTicket(id);
        if (status == TicketService.ActionStatus.DELETE_FAILED) {
            return ResponseEntity.badRequest().body("票券刪除失敗");
        }
        return ResponseEntity.ok("票券已刪除");
    }
}

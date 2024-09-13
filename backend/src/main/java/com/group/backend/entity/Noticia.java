package com.group.backend.entity;
import java.sql.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "noticia")
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_id")
    private Long notiId;

    @Column(name = "noti_data")
    private Date notiData;

    @Column(name = "noti_text")
    private String notiText;

    @Column(name = "por_id")
    private Integer porId;

    @Column(name = "noti_url", length = 2048)
    private String url;

    @ManyToOne
    @JoinColumn(name = "por_id", referencedColumnName = "por_id", insertable = false, updatable = false)
    private Portal portal;

    @Column(name = "res_id")
    private Integer resId;

    @ManyToOne
    @JoinColumn(name = "res_id", referencedColumnName = "res_id", insertable = false, updatable = false)
    private Portal reporte;

    // getters and setters

}

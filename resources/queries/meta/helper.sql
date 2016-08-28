-- name: sql-get-meta

SELECT
        md.*
FROM
        reverie_meta_data                      md
        INNER JOIN reverie_meta_page           mp  ON md.id = mp.meta_data_id
WHERE
        mp.page_serial = :serial

UNION ALL

SELECT
        md.*
FROM
        reverie_meta_data                      md
        INNER JOIN reverie_meta_set_meta_data  smd ON md.id = smd.meta_data_id
        INNER JOIN reverie_meta_set            s   ON smd.meta_set_id = s.id
        INNER JOIN reverie_meta_page           mp  ON mp.meta_set_id = s.id
WHERE
        mp.page_serial = :serial

UNION ALL


SELECT
        md.*
FROM
        reverie_meta_data                      md
        INNER JOIN reverie_meta_set_meta_data  smd ON md.id = smd.meta_data_id
        INNER JOIN reverie_meta_set            s   ON smd.meta_set_id = s.id
WHERE
        s.all_pages_p = true
;
